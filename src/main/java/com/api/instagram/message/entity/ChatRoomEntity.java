package com.api.instagram.message.entity;

import javax.persistence.*;

@Entity(name = "CHATROOM")
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
