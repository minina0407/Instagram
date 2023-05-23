package com.api.instagram.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizeUser {
    @NotBlank(message = "이메일이 입력되지 않았습니다.")
    @Email(message = "올바른 이메일을 입력해주세요")
    private String email;
    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    private String password;

    @Builder
    public AuthorizeUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
