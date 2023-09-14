package com.api.PortfoGram.user.entity;


import com.api.PortfoGram.auth.enums.AuthEnums;
import com.api.PortfoGram.chat.entity.UserChatRoomEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
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
    @Enumerated(EnumType.STRING) // Add this annotation to specify that it's an enum.
    private AuthEnums.ROLE role;

    @OneToMany(mappedBy = "follower")
    private List<FollowEntity> followers;

    @OneToMany(mappedBy = "following")
    private List<FollowEntity> followings;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
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
