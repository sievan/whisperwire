package com.jsievers.whisperwire.messages;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
class KafkaConfig {
    private final KafkaProperties kafkaProperties;
    @Value("${spring.kafka.ws-consumer.group-id}")
    private String wsGroupId;
    @Value("${spring.kafka.history-consumer.group-id}")
    private String historyGroupId;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    public String getBootstrapServers() {
        return kafkaProperties.getBootstrapServers();
    }

    public String getGroupId() {
        return kafkaProperties.getConsumer().getGroupId();
    }

    public String getAutoOffsetReset() {
        return kafkaProperties.getConsumer().getAutoOffsetReset();
    }

    // Base consumer factory configuration
    private Map<String, Object> getBaseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        Map<String, String> consumerProps = kafkaProperties.getConsumer().getProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, Integer.parseInt(consumerProps.get("heartbeat.interval.ms")));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, Integer.parseInt(consumerProps.get("session.timeout.ms")));
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        return props;
    }

    // Consumer factory for message history
    @Bean
    public ConsumerFactory<String, WMessage> historyConsumerFactory() {
        Map<String, Object> props = getBaseConsumerProps();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, historyGroupId);
        // Override in order to be able to set specific offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        JsonDeserializer<WMessage> deserializer = new JsonDeserializer<>(WMessage.class);
        deserializer.addTrustedPackages("com.jsievers.whisperwire.messages");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    // Consumer factory for websocket broadcasting
    @Bean
    public ConsumerFactory<String, WMessage> websocketConsumerFactory() {
        Map<String, Object> props = getBaseConsumerProps();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, wsGroupId);

        JsonDeserializer<WMessage> deserializer = new JsonDeserializer<>(WMessage.class);
        deserializer.addTrustedPackages("com.jsievers.whisperwire.messages");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    // Container factory for message history
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WMessage> kafkaHistoryListenerContainerFactory(
            ConsumerFactory<String, WMessage> historyConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, WMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(historyConsumerFactory);
        // Optional: Set specific offset to start from
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
            @Override
            public void onPartitionsAssigned(@NonNull Consumer<?, ?> consumer, @NonNull Collection<TopicPartition> partitions) {
                // Seek to specific offset or timestamp if needed
                // consumer.seek(partition, specificOffset);
                // Or seek to timestamp:
                // consumer.offsetsForTimes(timestampToSearch);
                partitions.forEach(partition -> {
                    consumer.seek(partition, 0);
                });
            }

            @Override
            public void onPartitionsRevoked(@NonNull Collection<TopicPartition> partitions) {
                log.info("Partitions revoked: {}", partitions);
            }
        });
        return factory;
    }

    // Container factory for websocket broadcasting
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WMessage> kafkaWsListenerContainerFactory(
            ConsumerFactory<String, WMessage> websocketConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, WMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(websocketConsumerFactory);
        return factory;
    }

    @Bean
    public ProducerFactory<String, WMessage> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, WMessage> kafkaTemplate(ProducerFactory<String, WMessage> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
