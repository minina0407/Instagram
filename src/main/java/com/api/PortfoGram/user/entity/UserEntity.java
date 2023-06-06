package com.api.PortfoGram.user.entity;


import com.api.PortfoGram.auth.enums.AuthEnums;
import com.api.PortfoGram.image.dto.Image;
import com.api.PortfoGram.user.dto.Profile;
import com.api.PortfoGram.user.dto.UserImage;
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
    private Long followers;
    @Column(name = "followings", nullable = false)
    private Long following;

    @Column(name = "self_introduction", nullable = false)
    private String selfIntroduction;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id", referencedColumnName = "id")
    private UserImageEntity profileImage;

    @Builder
    public UserEntity(Long id, String nickname, String password, String email, String name, AuthEnums.ROLE role, Long followers, Long following, String selfIntroduction, UserImageEntity profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
        this.followers = followers;
        this.following = following;
        this.selfIntroduction = selfIntroduction;
        this.profileImage = profileImage;
    }

    public void setNickname(String nickname) {
        this.nickname =nickname;
    }
    public void setSelfIntroduction(String selfIntroduction){
        this.selfIntroduction = selfIntroduction;
    }
    public void setProfileImage(UserImageEntity profileImage){
        this.profileImage = profileImage;
    }
    public void addImage(UserImageEntity userImageEntity) {
        if (this.profileImage != null) {
            this.profileImage.setUser(null);
        }
        this.profileImage = userImageEntity;
        if (userImageEntity != null) {
            userImageEntity.setUser(this);
        }
    }

}
