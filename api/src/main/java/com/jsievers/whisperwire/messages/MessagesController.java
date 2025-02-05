package com.jsievers.whisperwire.messages;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class MessagesController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/messages")
    public Messages getAllMessages(@RequestParam(required = false) String conversationId) {
        List<WMessage> messages = messageService.getAllMessages("test-topic", conversationId);

        return new Messages(messages);
    }

    @PostMapping("/messages")
    public String createMessage(@RequestBody CreateMessageRequest messageDTO, @RequestParam String conversationId) {
        WMessage message = messageDTO.toMessage(conversationId);
        return messageService.create(conversationId, message);
    }
}
