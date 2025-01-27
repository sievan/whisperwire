package com.jsievers.whisperwire.messages;

import org.springframework.stereotype.Service;

// Used for
@Service
public class KafkaConfigService {

    private final KafkaProperties kafkaProperties;

    public KafkaConfigService(KafkaProperties kafkaProperties) {
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
}