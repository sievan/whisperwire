package com.jsievers.whisperwire.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateMessageRequest(
        @JsonProperty("content") String content,
        @JsonProperty("author") String author
) {
    @JsonCreator
    public CreateMessageRequest {}
    
    public WMessage toMessage(String conversationId) {
        return WMessage.create(content, author, conversationId);
    }
}
