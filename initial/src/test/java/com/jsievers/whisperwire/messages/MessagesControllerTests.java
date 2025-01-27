package com.jsievers.whisperwire.messages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(MessagesController.class)
class MessagesControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Test
    void getAllMessages_ShouldReturnMessages() throws Exception {
        List<String> expectedMessages = Arrays.asList("message1", "message2");
        when(messageService.getAllMessages("test-topic")).thenReturn(expectedMessages);

        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages[0]").value("message1"));
    }

    @Test
    void createMessage_ShouldReturnId() throws Exception {
        WMessage message = new WMessage("test content", "test author", "test conversationId");
//        when(messageService.create(any(Message.class))).thenReturn("msg-123");

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"test content\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("msg-123"));
    }
}
