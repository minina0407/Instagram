package com.api.PortfoGram.chat.dto;

import com.api.PortfoGram.chat.entity.ChatMessageEntity;
import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
    private String sender;
    private Long senderId;
    private MessageType messageType;
    private Long receiverId;
    private String content;
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    private Long chatRoomId;
    @Builder
    public ChatMessage(Long id, Long senderId,String sender,  MessageType messageType, Long receiverId, String content, LocalDateTime createdAt, Long chatRoomId) {
        this.id = id;
        this.senderId = senderId;
        this.messageType = messageType;
        this.receiverId = receiverId;
        this.content = content;
        this.sender = sender;
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
    public void setSenderId(Long senderId) {
            this.senderId = senderId;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setReceiverId(Long receiverId){
        this.receiverId = receiverId;
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
