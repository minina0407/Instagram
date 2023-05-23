package com.api.instagram.auth.utils;

import com.api.instagram.exception.dto.BadRequestException;
import com.api.instagram.exception.dto.ExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";

    @Value("${jwt.expirationDateInMinute}")
    private long accessTokenExpireTime;
    @Value("${jwt.refreshTokenExpirationDateInMinute}")
    private long refreshTokenExpireTime;
    @Value("${jwt.refreshSecret}")
    private String refreshSecret;
    @Value("${jwt.secret}")
    private String secret;
    private Key key;
    private Key refreshKey;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        byte[] refreshKeyBytes = Base64.getDecoder().decode(refreshSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public String createAccessToken(Authentication authentication, String authorities) {
        long now = System.currentTimeMillis();
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(now + accessTokenExpireTime * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return accessToken;
    }

    public String createRefreshToken(Authentication authentication, String authorities) {
        long now = System.currentTimeMillis();
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(now + refreshTokenExpireTime * 60 * 1000))
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .compact();

        return refreshToken;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_TOKEN_INVALID, "권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Authentication getRefreshAuthentication(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_TOKEN_INVALID, "권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private Claims parseRefreshClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            logger.info("Invalid JWT token.");
        }
        return false;
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            logger.info("Invalid JWT token.");
        }
        return false;
    }
}
