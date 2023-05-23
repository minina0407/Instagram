package com.api.instagram.user.service;

import com.api.instagram.exception.dto.BadRequestException;
import com.api.instagram.exception.dto.ExceptionEnum;
import com.api.instagram.file.FileService;
import com.api.instagram.user.UserRepository;
import com.api.instagram.user.dto.Profile;
import com.api.instagram.user.dto.User;
import com.api.instagram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional
    public Profile searchProfileById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"User not found"));

        return Profile.builder()
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .followers(user.getFollowers())
                .following(user.getFollowing())
                .build();
    }

    @Transactional
    public User createUser(String nickname, MultipartFile profileImage) throws IOException {
        String profileImageUrl = null;
        if (profileImage != null) {
            profileImageUrl = fileService.upload(profileImage);
        }

        UserEntity user = UserEntity.builder()
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();

        UserEntity savedUser = userRepository.save(user);

        return User.builder()
                .id(savedUser.getId())
                .nickname(savedUser.getNickname())
                .profileImageUrl(savedUser.getProfileImageUrl())
                .build();
    }

    @Transactional
    public void deleteMember(Long memberId) {
        UserEntity userEntity = userRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("Member not found"));

        userRepository.delete(userEntity);
    }
}
