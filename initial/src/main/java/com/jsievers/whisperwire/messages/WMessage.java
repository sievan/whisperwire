package com.jsievers.whisperwire.messages;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record WMessage(
        @JsonProperty("content") String content,
        @JsonProperty("author") String author,
        @JsonProperty("conversationId") String conversationId
) {
    @JsonCreator
    public WMessage {
    }
}