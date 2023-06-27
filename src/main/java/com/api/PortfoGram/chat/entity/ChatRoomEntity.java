package com.api.PortfoGram.chat.entity;


import com.api.PortfoGram.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room")
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatRoom")
    private List<UserChatRoomEntity> userChatRooms = new ArrayList<>();


    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessageEntity> chatMessages = new ArrayList<>();

    @Builder
    public ChatRoomEntity(Long senderId, Long receiverId, LocalDateTime createdAt) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
    }
    public void addChatMessage(ChatMessageEntity chatMessage) {
        chatMessages.add(chatMessage);
    }
    public List<UserEntity> getUsers() {
        List<UserEntity> users = new ArrayList<>();
        for (UserChatRoomEntity userChatRoom : userChatRooms) {
            users.add(userChatRoom.getUser());
        }
        return users;
    }
}
