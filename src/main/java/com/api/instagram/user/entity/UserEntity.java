package com.api.instagram.user.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class UserEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private long followers;
    private long following;

    @Builder
    public UserEntity(Long id, String nickname, String password, String email, String name, String profileImageUrl) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }


}
