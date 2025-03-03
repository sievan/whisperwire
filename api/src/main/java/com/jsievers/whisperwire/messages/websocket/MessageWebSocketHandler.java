package com.jsievers.whisperwire.messages.websocket;

import org.apache.http.NameValuePair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {
    // Map of conversationId to list of sessions
    private final ConcurrentHashMap<String, Set<WebSocketSession>> conversationSessions = new ConcurrentHashMap<>();

    public void registerSession(String conversationId, WebSocketSession session) {
        conversationSessions.computeIfAbsent(conversationId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Extract conversationId from URI parameters
        String conversationId = extractConversationId(session.getUri());
        registerSession(conversationId, session);
    }

    private String extractConversationId(URI uri) {
        // Example URI: ws://host/ws?conversationId=123
        return URLEncodedUtils.parse(uri, StandardCharsets.UTF_8)
                .stream()
                .filter(pair -> "conversationId".equals(pair.getName()))
                .findFirst()
                .map(NameValuePair::getValue)
                .orElseThrow(() -> new IllegalArgumentException("conversationId is required"));
    }

    public void broadcastToConversation(String conversationId, String message) {
        Set<WebSocketSession> sessions = conversationSessions.get(conversationId);
        if (sessions != null) {
            TextMessage textMessage = new TextMessage(message);
            sessions.removeIf(session -> !session.isOpen());
            sessions.forEach(session -> {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    // Handle error
                }
            });
        }
    }
}
