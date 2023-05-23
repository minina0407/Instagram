package com.api.instagram.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Profile {
    private String nickname;
    private String profileImageUrl;
    private long followers;
    private long following;

    @Builder
    public Profile(String nickname, String profileImageUrl, long followers, long following) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.followers = followers;
        this.following = following;
    }
}
