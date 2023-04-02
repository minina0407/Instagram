package com.api.instagram.message.entity;

import com.api.instagram.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "MESSAGE")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserEntity senderId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoomEntity roomId;

    @Column(name = "content")
    private String content;

    @Column(name = "state")
    private String state;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
