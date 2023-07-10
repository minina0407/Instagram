package com.api.PortfoGram.chat.entity;

import com.api.PortfoGram.chat.dto.ChatMessage;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_message")
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoomEntity chatRoom;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatMessageEntity(Long id,ChatRoomEntity chatRoom, Long senderId, Long receiverId, String content, LocalDateTime createdAt) {
        this.chatRoom = chatRoom;
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.createdAt = createdAt;
    }
    public ChatMessage toEntity(ChatMessageEntity chatMessageEntity){
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
