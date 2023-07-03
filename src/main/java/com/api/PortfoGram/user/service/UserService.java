package com.api.PortfoGram.user.service;

import com.api.PortfoGram.auth.enums.AuthEnums;
import com.api.PortfoGram.auth.utils.SecurityUtil;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;


import com.api.PortfoGram.user.dto.Profile;
import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.entity.FollowEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;


    @Transactional
    public Profile searchProfileById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "User not found"));

        return Profile.builder()
                .nickname(user.getNickname())
                .followers(user.getFollowers().stream().count())
                .following(user.getFollowings().stream().count())
                .build();
    }

    @Transactional
    public UserEntity getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND));
    }

    @Transactional
    public void saveUser(User user) {
        if (user.getEmail() == null)
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "이미 가입되어있는 이메일입니다.");

        if (user.getNickname() == null)
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "중복된 닉네임입니다.");

        UserEntity userEntity = UserEntity.builder()
                .nickname(user.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(AuthEnums.ROLE.ROLE_USER)
                .build();

        userRepository.save(userEntity);

    }


    @Transactional
    public void deleteMember(Long memberId) {
        UserEntity userEntity = userRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("Member not found"));

        userRepository.delete(userEntity);
    }

    public UserEntity findById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Member not found"));

        return userEntity;
    }
}
