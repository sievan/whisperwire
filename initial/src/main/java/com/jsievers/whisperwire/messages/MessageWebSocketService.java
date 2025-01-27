package com.jsievers.whisperwire.messages;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageWebSocketService {
    private final MessageWebSocketHandler websocketHandler;

    @KafkaListener(
            topics = "test-topic",
            containerFactory = "kafkaWmListenerContainerFactory",
            groupId = "ws-group"
    )
    public void listen(WMessage message) {
//        WMessage message = record.value();
        // Create JSON or format message content as needed
        System.out.println("Broadcasting: " + message);
        String formattedMessage = String.format("{\"content\":\"%s\",\"author\":\"%s\"}",
                message.content(), message.author());
        websocketHandler.broadcastToConversation(message.conversationId(), formattedMessage);
    }
}
