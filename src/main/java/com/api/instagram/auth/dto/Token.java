package com.api.instagram.auth.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Token {
    private String accessToken;
    private String refreshToken;

    @Builder
    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


}
