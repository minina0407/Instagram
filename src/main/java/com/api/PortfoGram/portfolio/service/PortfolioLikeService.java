package com.api.PortfoGram.portfolio.service;

import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import com.api.PortfoGram.portfolio.entity.PortfolioLikeEntity;
import com.api.PortfoGram.portfolio.repository.PortfolioLikeRepository;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PortfolioLikeService {
    private final PortfolioLikeRepository portfolioLikeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;

    @Transactional
    public void likePortfolio(Long portfolioId) {
        if (portfolioId == null) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "포트폴리오를 찾을 수 없습니다");
        }

        UserEntity userEntity = userService.getMyUserWithAuthorities();

        boolean existsInDB = portfolioLikeRepository.existsByUserIdAndPortfolioId(userEntity.getId(), portfolioId);
        String redisKey = "user:" + userEntity.getId() + ":likes";
        String modifiedUsersKey = "modifiedUsers";
        boolean existsInRedis = redisTemplate.opsForSet().isMember(redisKey, String.valueOf(portfolioId));

        if (!existsInDB && !existsInRedis) {
            PortfolioLikeEntity portfolioLikeEntity = PortfolioLikeEntity.builder()
                    .user(userEntity)
                    .portfolio(PortfolioEntity.builder().id(portfolioId).build())
                    .build();
            portfolioLikeRepository.save(portfolioLikeEntity);

            redisTemplate.opsForSet().add(redisKey, String.valueOf(portfolioId));
            redisTemplate.expire(redisKey, Duration.ofDays(7));

            redisTemplate.opsForSet().add(modifiedUsersKey, String.valueOf(userEntity.getId()));
        }
    }

    @Transactional
    @Scheduled(cron = "0 * * * *")
    public void syncRedisToDB() {
        String modifiedUsersKey = "modifiedUsers";
        Set<String> modifiedUserIds = redisTemplate.opsForSet().members(modifiedUsersKey);

        for (String userIdStr : modifiedUserIds) {
            Long userId = Long.parseLong(userIdStr);
            UserEntity userEntity = userService.findById(userId);

            String redisKey = "user:" + userEntity.getId() + ":likes";
            Set<String> redisLikes = redisTemplate.opsForSet().members(redisKey);

            if (redisLikes != null && !redisLikes.isEmpty()) {
                for (String like : redisLikes) {
                    Long portfolioId = Long.parseLong(like);

                    boolean existsInDB = portfolioLikeRepository.existsByUserIdAndPortfolioId(userEntity.getId(), portfolioId);
                    if (!existsInDB) {
                        PortfolioLikeEntity portfolioLikeEntity = PortfolioLikeEntity.builder()
                                .user(userEntity)
                                .portfolio(PortfolioEntity.builder().id(portfolioId).build())
                                .build();
                        portfolioLikeRepository.save(portfolioLikeEntity);
                    }
                }

                redisTemplate.delete(redisKey);
            }
        }

        redisTemplate.delete(modifiedUsersKey);
    }
}
