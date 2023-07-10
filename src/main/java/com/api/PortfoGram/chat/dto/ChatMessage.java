package com.api.PortfoGram.chat.dto;

import com.api.PortfoGram.chat.entity.ChatMessageEntity;
import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    private Long senderId;
    private String sender;
    private MessageType messageType;
    private Long receiverId;
    private String content;
    private LocalDateTime createdAt;
    private Long chatRoomId;
    @Builder
    public ChatMessage(Long id, Long senderId, String sender, MessageType messageType, Long receiverId, String content, LocalDateTime createdAt, Long chatRoomId) {
        this.id = id;
        this.senderId = senderId;
        this.sender = sender;
        this.messageType = messageType;
        this.receiverId = receiverId;
        this.content = content;
        this.createdAt = createdAt;
        this.chatRoomId = chatRoomId;
    }

    public enum MessageType {
        JOIN, MESSAGE;
    }

    public ChatMessageEntity toEntity() {
        return ChatMessageEntity.builder()
                .id(this.id)
                .senderId(this.senderId)
                .receiverId(this.receiverId)
                .content(this.content)
                .createdAt(this.createdAt)
                .chatRoom(ChatRoomEntity.builder().id(this.chatRoomId).build())
                .build();
    }
    public static ChatMessage fromEntity(ChatMessageEntity chatMessageEntity) {
        return ChatMessage.builder()
                .id(chatMessageEntity.getId())
                .senderId(chatMessageEntity.getSenderId())
                .receiverId(chatMessageEntity.getReceiverId())
                .content(chatMessageEntity.getContent())
                .createdAt(chatMessageEntity.getCreatedAt())
                .chatRoomId(chatMessageEntity.getChatRoom().getId())
                .build();
    }
}
