package com.api.PortfoGram.chat.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChatRoom {
    private Long id;
    private Long userId;
    private Long chatRoomId;

    public UserChatRoom(Long id, Long userId, Long chatRoomId) {
        this.id = id;
        this.userId = userId;
        this.chatRoomId = chatRoomId;
    }
}