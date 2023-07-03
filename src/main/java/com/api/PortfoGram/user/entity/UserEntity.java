package com.api.PortfoGram.user.entity;


import com.api.PortfoGram.auth.enums.AuthEnums;
import com.api.PortfoGram.chat.entity.UserChatRoomEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "follower")
    private List<FollowEntity> followers;

    @OneToMany(mappedBy = "following")
    private List<FollowEntity> followings;


    @OneToOne(mappedBy = "user")
    private ProfileImageEntity profileImage;


    @OneToMany(mappedBy = "user")
    private List<UserChatRoomEntity> userChatRooms;

    @Builder
    public UserEntity(Long id, String nickname, String password, String email, String name, AuthEnums.ROLE role, List<FollowEntity> followers, List<FollowEntity> followings, ProfileImageEntity profileImage, List<UserChatRoomEntity> userChatRooms) {
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
