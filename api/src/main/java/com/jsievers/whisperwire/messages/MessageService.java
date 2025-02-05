package com.jsievers.whisperwire.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;


@Service
public class MessageService {

    private final KafkaTemplate<String, WMessage> kafkaTemplate;
    private final ConcurrentLinkedDeque<WMessage> recentMessages = new ConcurrentLinkedDeque<>();
    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<WMessage>> recentMessagesMap = new ConcurrentHashMap<>();
    private static final int MAX_RECENT_MESSAGES = 20;

    private final ObjectMapper mapper = new ObjectMapper();

    public MessageService(KafkaTemplate<String, WMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    private void addMessage(WMessage message) {
        String key = message.conversationId();

        recentMessagesMap.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>()).addFirst(message);
    }

    @KafkaListener(
            topics = "test-topic",
            containerFactory = "kafkaHistoryListenerContainerFactory",
            concurrency = "3"  // Number of consumer threads
    )
    public void listen(WMessage message) {
        recentMessages.addFirst(message);
        addMessage(message);
    }

    public List<WMessage> getAllMessages(String topic, String conversationId) {
        if(conversationId == null) {
            return new ArrayList<>(recentMessages);
        } else if (!recentMessagesMap.containsKey(conversationId)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(recentMessagesMap.get(conversationId));
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