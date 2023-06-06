package com.api.PortfoGram.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Profile {
    private String nickname;
    private Long followers;
    private Long following;
    private String selfIntroduction;

    @Builder
    public Profile(String nickname,  Long followers, Long following,String selfIntroduction) {
        this.nickname = nickname;
        this.followers = followers;
        this.following = following;
        this.selfIntroduction = selfIntroduction;
    }
}
