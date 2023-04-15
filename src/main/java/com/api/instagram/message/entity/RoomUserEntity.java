package com.api.instagram.message.entity;

import com.api.instagram.user.entity.UserEntity;

import javax.persistence.*;

@Entity(name = "ROOMUSER")
public class RoomUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @ManyToOne
    @JoinColumn(name = "chatting_room_id")
    private ChatRoomEntity chattingRoomId;
}
