package com.jsievers.whisperwire.messages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MessagesController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/messages")
    public Messages getAllMessages() {
        String testString = "hej";
        List<String> messages = messageService.getAllMessages("test-topic");

        return new Messages(messages);
    }

    @PostMapping("/messages")
    public String createMessage(@RequestBody WMessage message) {
        return messageService.create(message);
    }
}
