package com.api.PortfoGram.user.service;

import com.api.PortfoGram.user.entity.FollowEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.repository.FollowRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserService userService;
    private final FollowRepository followRepository;
    private  final StringRedisTemplate stringRedisTemplate;
    @Transactional
    public void followUser(Long followingId) {
        UserEntity user = userService.getMyUserWithAuthorities();
        UserEntity followUser = userService.findById(followingId);

        // 중복 팔로우 체크
        boolean alreadyFollowing = user.getFollowings().stream()
                .anyMatch(followEntity -> followEntity.getFollowing().getId().equals(followingId));

        if (!alreadyFollowing) {
            FollowEntity followEntity = FollowEntity.builder()
                    .follower(user)
                    .following(followUser)
                    .build();
            user.getFollowings().add(followEntity);
            followRepository.save(followEntity);

            String redisKeyForCurrentUserFollowings = "user:" + user.getId() + ":followings";
            double timestampForNewFollowing= System.currentTimeMillis();
            stringRedisTemplate.opsForZSet().add(redisKeyForCurrentUserFollowings,
                    followingId.toString(),
                    timestampForNewFollowing);
        }
    }


    @Transactional
    public void unfollowUser(Long userId, Long followingId) {
        UserEntity user = userService.findById(userId);
        Optional<FollowEntity> existingFollow = user.getFollowings().stream()
                .filter(followEntity -> followEntity.getFollowing().getId().equals(followingId))
                .findFirst();

        if (existingFollow.isPresent()) {
            user.getFollowings().remove(existingFollow.get());
            followRepository.delete(existingFollow.get());
        }
    }
    @Transactional
    public List<Long> getFollowerIds(Long userId){
        List<Long> followerIds = followRepository.findByFollowingId(userId).stream()
                .collect(Collectors.toList());
        return followerIds;
    }
}
