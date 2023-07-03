package com.api.PortfoGram.auth.controller;


import com.api.PortfoGram.auth.dto.Token;
import com.api.PortfoGram.auth.service.TokenService;
import com.api.PortfoGram.user.dto.AuthorizeUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.transform.Result;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class TokenController {
    private final TokenService tokenService;
    @PostMapping("/login")
    public ResponseEntity<Token> login(@Valid @RequestBody AuthorizeUser user) {
        Token token = tokenService.login(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@Valid @RequestBody Token token) {
        tokenService.reissueToken(token);
        return new ResponseEntity<>( "토큰 정보가 갱신되었습니다.", HttpStatus.OK);
    }

}
