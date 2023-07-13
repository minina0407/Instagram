package com.api.PortfoGram.portfolio.service;

import com.api.PortfoGram.Image.dto.Image;
import com.api.PortfoGram.portfolio.dto.Portfolio;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.Image.dto.Images;
import com.api.PortfoGram.Image.service.PortfolioImageService;
import com.api.PortfoGram.portfolio.dto.PortfolioImage;
import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import com.api.PortfoGram.portfolio.entity.PortfolioImageEntity;
import com.api.PortfoGram.portfolio.repository.PortfolioRepository;
import com.api.PortfoGram.user.entity.FollowEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import common.RedisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserService userService;
    private final PortfolioImageService portfolioImageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    @Transactional
    public void savePortfolio(String content, List<MultipartFile> imageFiles) {
        UserEntity user = userService.getMyUserWithAuthorities();
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .user(user)
                .content(content)
                .build();

        PortfolioEntity savedPortfolioEntity = portfolioRepository.save(portfolioEntity);

        Images uploadedImages = portfolioImageService.uploadImage(imageFiles);
        List<Image> imageList = uploadedImages.getImages();
        for (Image image : imageList) {
            PortfolioImageEntity portfolioImageEntity = PortfolioImageEntity.builder()
                    .image(image.toImageEntity())
                    .portfolio(savedPortfolioEntity)
                    .build();
            savedPortfolioEntity.addImage(portfolioImageEntity);
        }
        cacheLatestPortfolioForUser(user, savedPortfolioEntity.getId());

        // 수정된 부분: user.getFollowers()를 userEntity에서 user로 변경
        List<Long> followerIds = user.getFollowerIds();
        cacheNewPortfolioForFollowers(savedPortfolioEntity.getId(), followerIds);
    }
    private void cacheLatestPortfolioForUser(UserEntity user, Long portfolioId) {
        String redisKey = "user:" + user.getId() + ":portfolios";
        // 새로 생성된 포트폴리오 ID를 저장소의 첫 번째 요소로 삽입
        stringRedisTemplate.opsForList().leftPush(redisKey, String.valueOf(portfolioId));

    }
    public void cacheNewPortfolioForFollowers(Long newPortfolioId, List<Long> followerIds) {
        followerIds.forEach(followerId -> {
            String redisKey = "user:" + followerId + ":portfolios";
            stringRedisTemplate.opsForList().rightPush(redisKey, newPortfolioId.toString());
        });
    }
    public List<Portfolio> getLatestPortfolios() {
        UserEntity userEntity = userService.getMyUserWithAuthorities();
        String redisKey = "user:" + userEntity.getId() + ":portfolios";
        List<String> portfolioIds = stringRedisTemplate.opsForList().range(redisKey, 0, -1);

        if (portfolioIds != null && !portfolioIds.isEmpty()) {
            List<Long> portfolioIdsLong = portfolioIds.stream().map(Long::parseLong).collect(Collectors.toList());

            List<PortfolioEntity> portfolioEntities = portfolioRepository.findByIdInOrderByCreatedAtDesc(portfolioIdsLong);
            return portfolioEntities.stream().map(Portfolio::fromEntity).collect(Collectors.toList());
        } else {
            // 기존 저장소에서 마지막 수록된 포트폴리오 가져오고 Redis에 갱신하는 로직 추가
            List<PortfolioEntity> latestPortfolios = portfolioRepository.findLatestPortfoliosByUserId(userEntity.getId());
            if (latestPortfolios != null && !latestPortfolios.isEmpty()) {
                List<Long> portfolioIdsLong = latestPortfolios.stream().map(PortfolioEntity::getId).collect(Collectors.toList());
                redisTemplate.opsForList().rightPushAll(redisKey, portfolioIdsLong.toArray());
                return latestPortfolios.stream().map(Portfolio::fromEntity).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
    @Transactional(readOnly = true)
    public Portfolio getPortfolioById(Long portfolioId) {
        String redisKey = "portfolio:" + portfolioId;
        Portfolio portfolio = (Portfolio) redisTemplate.opsForValue().get(redisKey);

        if (portfolio == null) {
            PortfolioEntity portfolioEntity = portfolioRepository. findPortfolioEntityById(portfolioId)
                    .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "피드를 찾을 수 없습니다."));
            List<PortfolioImage> portfolioImage = portfolioEntity.getPortfolioImages()
                    .stream()
                    .map(PortfolioImage::fromEntity)
                    .collect(Collectors.toList());

            portfolio = Portfolio.builder()
                    .id(portfolioEntity.getId())
                    .userId(portfolioEntity.getUser().getId())
                    .content(portfolioEntity.getContent())
                    .postImages(portfolioImage)
                    .build();

            redisTemplate.opsForValue().set(redisKey, portfolio);
        }

        return portfolio;
    }

    @Transactional(readOnly = true)
    public PortfolioEntity getPortfolioEntityId(Long postId) {
        return portfolioRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "포트폴리오를  찾을 수 없습니다."));

    }

    @Transactional(readOnly = true)
    public List<Portfolio> getAllPortfolios() {
        List<PortfolioEntity> postsEntity = portfolioRepository.findAll();

        if (postsEntity.isEmpty()) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "포트폴리오가 없습니다.");
        }

        List<Portfolio> portfolios = postsEntity.stream()
                .map(Portfolio::fromEntity)
                .collect(Collectors.toList());

        return portfolios;
    }

    @Transactional
    public void updatePortfolio(Long id, Portfolio portfolioRequest) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "포트폴리오를 찾을 수 없습니다."));

        portfolioEntity.updateContent(portfolioRequest.getContent()); // 게시글 내용 업데이트

        portfolioRepository.save(portfolioEntity);

    }

    @Transactional
    public void deletePortfolio(Long id) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "프토폴리오를 찾을 수 없습니다."));

        portfolioRepository.delete(portfolioEntity);
    }

}