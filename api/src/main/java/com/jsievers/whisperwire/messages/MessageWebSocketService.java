package com.jsievers.whisperwire.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageWebSocketService {
    private final MessageWebSocketHandler websocketHandler;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(
            topics = "test-topic",
            containerFactory = "kafkaWsListenerContainerFactory"
    )
    public void listen(WMessage message) {
        try {
            String formattedMessage = mapper.writeValueAsString(message);
            websocketHandler.broadcastToConversation(message.conversationId(), formattedMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not format WMessage as json string", e);
        }
    }
}
