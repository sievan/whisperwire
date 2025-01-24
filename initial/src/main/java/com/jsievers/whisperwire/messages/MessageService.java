package com.jsievers.whisperwire.messages;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
public class MessageService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Autowired
    private KafkaConfigService kafkaConfigService;

    public List<String> getAllMessages(String topic) {
        KafkaConsumer<String, String> consumer = getKafkaConsumer(topic);

        TopicPartition partition = new TopicPartition("test-topic", 0);
        List<TopicPartition> partitions = new ArrayList<>();
        partitions.add(partition);
        consumer.assign(partitions);

        int messagesToRetrieve = 10;
        consumer.seekToEnd(partitions);
        long startIndex = Math.max(consumer.position(partition) - messagesToRetrieve, 0);
        consumer.seek(partition, startIndex);
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMinutes(1));

        List<String> messages = records.records(partition).stream().map(msg -> msg.value()).toList();

        return messages;
    }

    public String create(Message message) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafkaConfigService.getBootstrapServers());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");

        KafkaProducer<String, Message> producer = new KafkaProducer<>(properties);

        producer.send(new ProducerRecord<>("test-topic", message.conversationId(), message));

        return "OK";
    }

    private KafkaConsumer<String, String> getKafkaConsumer(String topic) {
        // Properties for the Kafka consumer
        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafkaConfigService.getBootstrapServers());
        properties.put("group.id", kafkaConfigService.getGroupId());
        properties.put("auto.offset.reset", kafkaConfigService.getAutoOffsetReset());  // Ensure we start from the earliest offset
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());

        // Create the Kafka consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        return consumer;
    }
}