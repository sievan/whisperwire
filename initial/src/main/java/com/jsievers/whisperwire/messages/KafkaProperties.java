package com.jsievers.whisperwire.messages;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private Consumer consumer;

    @Setter
    @Getter
    public static class Consumer {
        // Getters and setters
        private String groupId;
        private String autoOffsetReset;

    }

}
