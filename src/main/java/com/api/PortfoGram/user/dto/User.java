package com.api.PortfoGram.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class User {
    private Long id;
    private String nickname;
    private String email;
    private String name;
    private String profileImageUrl;
    @Builder
    public User(Long id, String nickname, String email, String name, String profileImageUrl) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

}