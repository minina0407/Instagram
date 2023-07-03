package com.api.PortfoGram.auth.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

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
