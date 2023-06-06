package com.api.PortfoGram.user.service;

import com.api.PortfoGram.auth.enums.AuthEnums;
import com.api.PortfoGram.auth.utils.SecurityUtil;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;

import com.api.PortfoGram.image.dto.Image;
import com.api.PortfoGram.image.dto.Images;
import com.api.PortfoGram.image.entity.ImageEntity;
import com.api.PortfoGram.image.service.ImageService;
import com.api.PortfoGram.user.UserRepository;
import com.api.PortfoGram.user.dto.Profile;
import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.dto.UserImage;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.entity.UserImageEntity;
import lombok.RequiredArgsConstructor;
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
    private final ImageService imageService;


    @Transactional
    public Profile getProfileById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "유저를 찾을 수 없습니다."));

        return Profile.builder()
                .nickname(user.getNickname())
                .followers(user.getFollowers())
                .following(user.getFollowing())
                .build();
    }

    @Transactional
    public UserEntity getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "본인이 아닙니다."));
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
    public void saveUserImage(MultipartFile userImage) throws IOException {
        UserEntity user = getMyUserWithAuthorities();

        Image uploadedUserImage = imageService.uploadFile(userImage);
        ImageEntity image = uploadedUserImage.toImageEntity();

        UserImageEntity userImageEntity = UserImageEntity.builder()
                .image(image)
                .user(user)
                .build();

        user.addImage(userImageEntity);

    }
    @Transactional
    public Profile updateMyProfile(Profile profile) {
        UserEntity currentUser = getMyUserWithAuthorities();

        UserEntity user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "유저를 찾을 수 없습니다."));

        if (profile != null) {
            user.setNickname(profile.getNickname());
            user.setSelfIntroduction(profile.getSelfIntroduction());
        }

        UserEntity updatedUser = userRepository.save(user);

        Profile updatedProfile = Profile.builder()
                .nickname(updatedUser.getNickname())
                .selfIntroduction(updatedUser.getSelfIntroduction())
                .build();

        return updatedProfile;

    }

    @Transactional
    public void deleteMember(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("유저를 찾을 수 없습니다"));

        userRepository.delete(userEntity);
    }
}
