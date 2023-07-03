package com.api.PortfoGram.post.service;

import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.post.entity.PostLikeEntity;
import com.api.PortfoGram.post.repository.PostLikeRepository;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final PostService postService;

    public void likePost(Long postId) {
        UserEntity userEntity = userService.getMyUserWithAuthorities();
        if (!postLikeRepository.existsByUserIdAndPostId(userEntity.getId(), postId)) {
            PostLikeEntity postLikeEntity = PostLikeEntity.builder()
                    .user(userEntity)
                    .post(PostEntity.builder().id(postId).build())
                    .build();
            postLikeRepository.save(postLikeEntity);
            postService.incrementLikeCount(postId);

            String redisKey = "user:" + userEntity.getId() + ":likes";
            redisTemplate.opsForSet().add(redisKey, String.valueOf(postId));

            // Redis와 DB 동기화
            syncRedisToDB(redisKey, postId);
        }
    }

    private void syncRedisToDB(String redisKey, Long postId) {
        Set<String> redisLikes = redisTemplate.opsForSet().members(redisKey);
        if (redisLikes == null || redisLikes.isEmpty()) {
            return; // Redis에 저장된 좋아요 정보가 없음
        }

        for (String like : redisLikes) {
            Long userId = Long.parseLong(like);
            PostLikeEntity postLikeEntity = PostLikeEntity.builder()
                    .user(UserEntity.builder().id(userId).build())
                    .post(PostEntity.builder().id(postId).build())
                    .build();
            postLikeRepository.save(postLikeEntity);
        }

    }
}
