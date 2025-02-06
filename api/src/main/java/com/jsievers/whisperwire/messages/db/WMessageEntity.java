package com.jsievers.whisperwire.messages.db;

import com.jsievers.whisperwire.messages.WMessage;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

import static java.lang.Integer.parseInt;

@Getter
@Entity
@Table(name = "messages")
public class WMessageEntity {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    private String content;
    private String author;
    private int conversationId;

    // Default constructor for JPA
    protected WMessageEntity() {}

    // Constructor for your use
    public WMessageEntity(UUID id, String content, String author, int conversationId) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.conversationId = conversationId;
    }

    public static WMessageEntity fromMessage(WMessage message) {
        return new WMessageEntity(
                UUID.fromString(message.id()),
                message.content(),
                message.author(),
                parseInt(message.conversationId())
        );
    }

}
