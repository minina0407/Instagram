package com.api.PortfoGram.user.dto;

import com.api.PortfoGram.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class User implements Serializable {
    private Long id;
    private String nickname;
    private String email;
    private String name;
    private String password;
    @Builder
    public User(Long id, String nickname, String email, String name, String password) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static User fromEntity(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }
}