package com.jsievers.whisperwire.messages.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsievers.whisperwire.messages.dto.WMessage;

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
