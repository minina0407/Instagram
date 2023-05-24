package com.api.PortfoGram.auth.service;


import com.api.PortfoGram.auth.dto.Token;
import com.api.PortfoGram.auth.utils.JwtTokenProvider;
import com.api.PortfoGram.user.dto.AuthorizeUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Token login(AuthorizeUser user) {

        UsernamePasswordAuthenticationToken authenticationToken = user.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = jwtTokenProvider.createAccessToken(authentication, authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication, authorities);

        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return token;
    }
}
