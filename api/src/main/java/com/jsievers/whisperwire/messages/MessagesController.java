package com.jsievers.whisperwire.messages;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsievers.whisperwire.messages.exception.MissingConversationIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "*")
@RestController
public class MessagesController {
    @Autowired
    private MessageService messageService;

    @ExceptionHandler(MissingConversationIdException.class)
    public ResponseEntity<String> handleMissingConversationId(MissingConversationIdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @GetMapping("/messages")
    public Messages getAllMessages(@RequestParam(required = false) String conversationId) {
        List<WMessage> messages = messageService.getAllConversationMessages("test-topic", conversationId);
        return new Messages(messages);
    }

    @PostMapping("/messages")
    public String createMessage(@RequestBody CreateMessageRequest messageDTO, @RequestParam String conversationId) {
        WMessage message = messageDTO.toMessage(conversationId);
        return messageService.create(message);
    }
}
