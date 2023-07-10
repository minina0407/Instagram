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
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final RedisTemplate redisTemplate;

    @Transactional
    public void savePortfolio(String content, List<MultipartFile> imageFiles) {
        UserEntity user = userService.getMyUserWithAuthorities();
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .user(user)
                .content(content)
                .build();

        PortfolioEntity savedPortfolioEntity = portfolioRepository.save(portfolioEntity);

        // 팔로워들의 타임라인에 기록
        Set<Long> followers = userService.getFollowers(user.getId());
        for (Long followerId : followers) {
            updateLatestPortfoliosInRedis(savedPortfolioEntity.getId(), "user:" + followerId + ":portfolios");
        }

        Images uploadedImages = portfolioImageService.uploadImage(imageFiles);
        List<Image> imageList = uploadedImages.getImages();
        for (Image image : imageList) {
            PortfolioImageEntity portfolioImageEntity = PortfolioImageEntity.builder()
                    .image(image.toImageEntity())
                    .portfolio(savedPortfolioEntity)
                    .build();
            savedPortfolioEntity.addImage(portfolioImageEntity);
        }
    }

    // Redis에 최신 포트폴리오를 업데이트하는 메서드입니다.
    public void updateLatestPortfoliosInRedis(Long portfolioId, String redisKey) {
        redisTemplate.opsForList().leftPush(redisKey, String.valueOf(portfolioId));
    }

    public List<Portfolio> getLatestPortfolios(Long userId) {
        String redisKey = "user:" + userId + ":portfolios";
        List<String> portfolioIds = redisTemplate.opsForList().range(redisKey, 0, -1);

        if (portfolioIds != null && !portfolioIds.isEmpty()) {
            List<Long> portfolioIdsLong = portfolioIds.stream().map(Long::parseLong).collect(Collectors.toList());

            List<PortfolioEntity> portfolioEntities = portfolioRepository.findByIdInOrderByCreatedAtDesc(portfolioIdsLong);
            return portfolioEntities.stream().map(Portfolio::fromEntity).collect(Collectors.toList());
        } else {
            // 기존 저장소에서 마지막 수록된 포트폴리오 가져오고 Redis에 갱신하는 로직 추가
            List<PortfolioEntity> latestPortfolios = portfolioRepository.findLatestPortfoliosByUserId(userId);
            if (latestPortfolios != null && !latestPortfolios.isEmpty()) {
                List<Long> portfolioIdsLong = latestPortfolios.stream().map(PortfolioEntity::getId).collect(Collectors.toList());
                redisTemplate.opsForList().rightPushAll(redisKey, portfolioIdsLong.toArray());
                return latestPortfolios.stream().map(Portfolio::fromEntity).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
    public void updateLatestPortfoliosInRedis(PortfolioEntity portfolioEntity, String redisKey) {
        String portfolioIdStr = String.valueOf(portfolioEntity.getId());
        redisTemplate.opsForList().leftPush(redisKey, portfolioIdStr);
    }

    @Transactional
    public List<Portfolio> getFollowedPortfolios(Long userId) {
        // Redis key를 생성합니다.
        String redisKey = "user:" + userId + ":followedPortfolios";
        Set<String> followedPortfolioIds = redisTemplate.opsForSet().members(redisKey); // Redis에 팔로우한 포트폴리오 ID 목록이 있는 경우
        if (followedPortfolioIds != null && !followedPortfolioIds.isEmpty()) {
            List<Long> portfolioIds = followedPortfolioIds.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<PortfolioEntity> followedPortfolioEntities = portfolioRepository.findAllById(portfolioIds);
            // 엔티티를 DTO로 변환하여 반환
            return followedPortfolioEntities.stream()
                    .map(Portfolio::fromEntity)
                    .collect(Collectors.toList());
        }
        // Redis에 팔로우한 포트폴리오 ID 목록이 없는 경우
        List<PortfolioEntity> followedPortfolioEntities = portfolioRepository.findFollowedPortfolios(userId);
        List<Portfolio> followedPortfolios = followedPortfolioEntities.stream()
                .map(Portfolio::fromEntity)
                .collect(Collectors.toList());
        // 조회한 팔로우한 포트폴리오 목록이 비어있지 않으면 Redis에 업데이트
        if (!followedPortfolios.isEmpty()) {
            updateFollowedPortfoliosInRedis(followedPortfolios, redisKey);
        }

        return followedPortfolios;
    }
    public void updateFollowedPortfoliosInRedis(List<Portfolio> followedPortfolios, String redisKey) {
        Set<String> updatedPortfolioIds = followedPortfolios.stream()
                .map(portfolioDto -> String.valueOf(portfolioDto.getId()))
                .collect(Collectors.toSet());

        redisTemplate.opsForSet().add(redisKey, updatedPortfolioIds.toArray(new String[0]));
    }
    @Transactional(readOnly = true)
    public Portfolio getPortfolioById(Long portfolioId) {
        String redisKey = "portfolio:" + portfolioId;
        Portfolio portfolio = (Portfolio) redisTemplate.opsForValue().get(redisKey);

        if (portfolio == null) {
            PortfolioEntity portfolioEntity = portfolioRepository.findPostById(portfolioId)
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