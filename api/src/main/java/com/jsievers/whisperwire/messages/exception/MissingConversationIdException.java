package com.jsievers.whisperwire.messages.exception;

public class MissingConversationIdException extends RuntimeException{
    public MissingConversationIdException(String message) {
        super(message);
    }
}
