package com.api.PortfoGram.user.service;

import com.api.PortfoGram.auth.enums.AuthEnums;
import com.api.PortfoGram.auth.utils.SecurityUtil;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.portfolio.dto.Portfolio;
import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import com.api.PortfoGram.portfolio.service.PortfolioService;
import com.api.PortfoGram.user.dto.Follow;
import com.api.PortfoGram.user.dto.Profile;
import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.entity.FollowEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public Profile searchProfileById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "유저를 찾을 수 없습니다."));

        return Profile.builder()
                .nickname(user.getNickname())
                .followers(user.getFollowers().stream().count())
                .following(user.getFollowings().stream().count())
                .build();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream()
                .map(User::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserEntity getMyUserWithAuthorities() {
        UserEntity userEntity = SecurityUtil.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND));

        return userEntity;
    }
    public Set<Long> getFollowers(Long userId) {
        String redisKey = "user:" + userId + ":followers";
        Set<String> followerIds = redisTemplate.opsForSet().members(redisKey);

        if (followerIds != null && !followerIds.isEmpty()) {
            return followerIds.stream().map(Long::parseLong).collect(Collectors.toSet());
        } else {
            // 기존 저장소에서 팔로워 목록을 가져오고 Redis에 갱신하는 로직 추가
            Set<Long> followers = userRepository.findFollowerIdsById(userId);
            if (followers != null && !followers.isEmpty()) {
                redisTemplate.opsForSet().add(redisKey, Arrays.toString(followers.toArray()));
            }
        }
        return new HashSet<>();
    }
    @Transactional
    public void saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "이미 가입되어 있는 이메일입니다.");
        }

        if (userRepository.existsByNickname(user.getNickname())) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "중복된 닉네임입니다.");
        }

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
    public void followUser(Long followingId) {
        UserEntity user = getMyUserWithAuthorities();
        UserEntity followUser = findById(followingId);

        // 중복 팔로우 체크
        boolean alreadyFollowing = user.getFollowings().stream()
                .anyMatch(followEntity -> followEntity.getFollowing().getId().equals(followingId));

        if (!alreadyFollowing) {
            FollowEntity followEntity = FollowEntity.builder()
                    .follower(user)
                    .following(followUser)
                    .build();
            user.getFollowings().add(followEntity);
            userRepository.save(user);
        }
    }

    @Transactional
    public void unfollowUser(Long userId, Long followingId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<FollowEntity> existingFollow = user.getFollowings().stream()
                .filter(followEntity -> followEntity.getFollowing().getId().equals(followingId))
                .findFirst();

        if (existingFollow.isPresent()) {
            user.getFollowings().remove(existingFollow.get());
            userRepository.save(user);
        }
    }

    private String getRedisKey(Long userId) {
        return "user:" + userId + ":followedPortfolios";
    }

    @Transactional
    public void deleteMember(Long memberId) {
        UserEntity userEntity = userRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"유저를 찾을 수 없습니다."));

        userRepository.delete(userEntity);
    }

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"유저를 찾을 수 없습니다."));
    }
}
