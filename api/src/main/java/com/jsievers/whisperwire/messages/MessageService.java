package com.jsievers.whisperwire.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final KafkaTemplate<String, WMessage> kafkaTemplate;
    private final Deque<WMessage> recentMessages = new ConcurrentLinkedDeque<>();
    private static final int MAX_RECENT_MESSAGES = 20;

    private final ObjectMapper mapper = new ObjectMapper();

    public MessageService(KafkaTemplate<String, WMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "test-topic",
            containerFactory = "kafkaHistoryListenerContainerFactory",
            concurrency = "3"  // Number of consumer threads
    )
    public void listen(WMessage message) {
        recentMessages.addFirst(message);
        while (recentMessages.size() > MAX_RECENT_MESSAGES) {
            recentMessages.removeLast();
        }
    }

    public List<WMessage> getAllMessages(String topic) {
        return new ArrayList<>(recentMessages);
    }

    public String create(String conversationId, WMessage message) {
        try {
            SendResult<String, WMessage> result = kafkaTemplate
                    .send("test-topic", message.conversationId(), message)
                    .get();
            return "OK";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error sending message", e);
        }
    }
}