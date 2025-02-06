package com.jsievers.whisperwire.messages;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record WMessage(
        @JsonProperty("id") String id,
        @JsonProperty("content") String content,
        @JsonProperty("author") String author,
        @JsonProperty("conversationId") String conversationId
) {
    @JsonCreator
    public WMessage {
    }

    public static WMessage create(String content, String author, String conversationId) {
        return new WMessage(UUID.randomUUID().toString(), content, author, conversationId);
    }
}
