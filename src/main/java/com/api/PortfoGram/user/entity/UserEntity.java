package com.api.PortfoGram.user.entity;


import com.api.PortfoGram.auth.enums.AuthEnums;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private long followers;
    @Column(name = "followings", nullable = false)
    private long following;

    @ManyToOne
    @JoinColumn(name = "profile_image_id", referencedColumnName = "image_id")
    private UserImageEntity profileImage;

    @Builder
    public UserEntity(Long id, String nickname, String password, String email, String name, AuthEnums.ROLE role, long followers, long following, UserImageEntity profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
        this.followers = followers;
        this.following = following;
        this.profileImage = profileImage;
    }





}
