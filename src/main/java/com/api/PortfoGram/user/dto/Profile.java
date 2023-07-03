package com.api.PortfoGram.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Profile {
    private String nickname;
    private String profileImageUrl;
    private Long followers;
    private Long following;

    @Builder
    public Profile(String nickname, String profileImageUrl, Long followers, Long following) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.followers = followers;
        this.following = following;
    }
}
