package com.api.PortfoGram.user.entity;


import com.api.PortfoGram.auth.enums.AuthEnums;
import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.api.PortfoGram.chat.entity.UserChatRoomEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class UserEntity {
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


    @Column(name = "role", nullable = false)
    private AuthEnums.ROLE role;

    @Column(name = "followers", nullable = false)
    private Long followers;
    @Column(name = "followings", nullable = false)
    private Long followings;


    @OneToOne(mappedBy = "user")
    private UserImageEntity profileImage;


    @OneToMany(mappedBy = "user")
    private List<UserChatRoomEntity> userChatRooms;

    @Builder
    public UserEntity(Long id, String nickname, String password, String email, String name, AuthEnums.ROLE role, Long followers, Long followings, UserImageEntity profileImage, List<UserChatRoomEntity> userChatRooms) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
        this.followers = followers;
        this.followings = followings;
        this.profileImage = profileImage;
        this.userChatRooms = userChatRooms;
    }



}
