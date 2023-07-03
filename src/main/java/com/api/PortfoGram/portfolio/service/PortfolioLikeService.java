package com.api.PortfoGram.portfolio.service;

import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import com.api.PortfoGram.portfolio.entity.PortfolioLikeEntity;
import com.api.PortfoGram.portfolio.repository.PortfolioLikeRepository;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PortfolioLikeService {
    private final PortfolioLikeRepository portfolioLikeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final PortfolioService portfolioService;

    public void likePortfolio(Long portfolioId) {
        UserEntity userEntity = userService.getMyUserWithAuthorities();
        if (!portfolioLikeRepository.existsByUserIdAndPortfolioId(userEntity.getId(), portfolioId)) {
            PortfolioLikeEntity portfolioLikeEntity = PortfolioLikeEntity.builder()
                    .user(userEntity)
                    .portfolio(PortfolioEntity.builder().id(portfolioId).build())
                    .build();
            portfolioLikeRepository.save(portfolioLikeEntity);
            portfolioService.incrementLikeCount(portfolioId);

            String redisKey = "user:" + userEntity.getId() + ":likes";
            redisTemplate.opsForSet().add(redisKey, String.valueOf(portfolioId));

        }
    }

    private void syncRedisToDB(String redisKey, Long portfolioId) {
        Set<String> redisLikes = redisTemplate.opsForSet().members(redisKey);
        if (redisLikes == null || redisLikes.isEmpty()) {
            return; // Redis에 저장된 좋아요 정보가 없음
        }

        for (String like : redisLikes) {
            Long userId = Long.parseLong(like);
            PortfolioLikeEntity portfolioLikeEntity = PortfolioLikeEntity.builder()
                    .user(UserEntity.builder().id(userId).build())
                    .portfolio(PortfolioEntity.builder().id(portfolioId).build())
                    .build();
            portfolioLikeRepository.save(portfolioLikeEntity);
        }
    }

}
