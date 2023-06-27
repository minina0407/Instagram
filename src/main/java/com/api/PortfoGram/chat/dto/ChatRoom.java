package com.api.PortfoGram.chat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

// ChatRoomDTO
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom implements Serializable {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    @Builder
    public ChatRoom(Long id, Long senderId, Long receiverId, LocalDateTime createdAt, String lastMessage, LocalDateTime lastMessageTime) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }
}