package com.api.PortfoGram.user.service;

import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.file.FileService;
import com.api.PortfoGram.user.UserRepository;
import com.api.PortfoGram.user.dto.Profile;
import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
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
