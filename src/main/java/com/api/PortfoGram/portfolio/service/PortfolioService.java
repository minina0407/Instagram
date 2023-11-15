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
import com.api.PortfoGram.user.service.FollowService;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserService userService;
    private final PortfolioImageService portfolioImageService;
    private final RedisTemplate redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final FollowService followService;

    @Transactional
    public void savePortfolio(String content, List<MultipartFile> imageFiles) {
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "컨텐츠 내용을 입력해주세요");
        }

        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "이미지가 없습니다.");
        }

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

        cacheLatestPortfolioForUser(user.getId().toString(), savedPortfolioEntity.getId());

        List<Long> followerIds = followService.getFollowerIds(user.getId());
        cacheNewPortfolioForFollowers(savedPortfolioEntity.getId().toString(), followerIds, savedPortfolioEntity.getCreatedAt().getTime());
    }

    private void cacheLatestPortfolioForUser(String userId, Long portfolioId) {
        String redisKey = "user:" + userId + ":portfolios";
        double timestamp = System.currentTimeMillis();

        stringRedisTemplate.opsForZSet().add(redisKey, portfolioId.toString(), timestamp);
    }

    private void cacheNewPortfolioForFollowers(String newPortfolioId, List<Long> followerIds, double timestamp) {
        followerIds.forEach(followerId -> {
            String redisKey = "user:" + followerId + ":portfolios";
            stringRedisTemplate.opsForZSet().add(redisKey, newPortfolioId, timestamp);
        });
    }

    public Page<Portfolio> getLatestPortfolios(UserEntity userEntity, Pageable pageable) {

        String redisKeyForCurrentUserFollowings = "user:" + userEntity.getId() + ":portfolios";
        Set<String> portfolioIds = stringRedisTemplate.opsForZSet().reverseRange(redisKeyForCurrentUserFollowings, 0, -1);

        List<Long> followedUserIdsAsLong = portfolioIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        Page<PortfolioEntity> portfolioEntityPage = portfolioRepository.findByIdIn(followedUserIdsAsLong, pageable);

        if (portfolioEntityPage.isEmpty()) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "사용자의 포트폴리오가 없습니다.");
        }
        Page<Portfolio> portfolio = portfolioEntityPage.map(Portfolio::fromEntity);
        return portfolio;
    }

    @Transactional(readOnly = true)
    public Portfolio getPortfolioById(Long portfolioId) {
        String redisKey = "portfolio:" + portfolioId;
        Portfolio portfolio = (Portfolio) redisTemplate.opsForValue().get(redisKey);

        if (portfolio == null) {
            PortfolioEntity portfolioEntity = portfolioRepository.findPortfolioEntityById(portfolioId)
                    .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "포트폴리오를 찾을 수 없습니다."));
            List<PortfolioImage> portfolioImage = portfolioEntity.getPortfolioImages()
                    .stream()
                    .map(PortfolioImage::fromEntity)
                    .collect(Collectors.toList());

            portfolio = Portfolio.builder()
                    .id(portfolioEntity.getId())
                    .userId(portfolioEntity.getUser().getId())
                    .content(portfolioEntity.getContent())
                    .portfolioImages(portfolioImage)
                    .createdAt(portfolioEntity.getCreatedAt())
                    .build();

            redisTemplate.opsForValue().set(redisKey, portfolio);
        }

        return portfolio;
    }

    @Transactional(readOnly = true)
    public Page<Portfolio> getAllPortfolios(Pageable pageable) {
        Page<PortfolioEntity> portfolioEntities = portfolioRepository.findAll(pageable);

        if (portfolioEntities.isEmpty()) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "포트폴리오가 없습니다.");
        }

        Page<Portfolio> portfolios = portfolioEntities.map(Portfolio::fromEntity);

        return portfolios;
    }

    @Transactional
    public void updatePortfolio(Long id, Portfolio portfolioRequest) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "포트폴리오를 찾을 수 없습니다."));

        if (portfolioRequest.getContent() != null && !portfolioRequest.getContent().isEmpty()) {
            // 게시글 내용 업데이트만 가능하게 함
            portfolioEntity.updateContent(portfolioRequest.getContent());
            portfolioRepository.save(portfolioEntity);
        }
        if (portfolioRequest.getContent() == null && portfolioRequest.getContent().isEmpty())
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_MISSING, "컨텐츠가 없습니다.");


    }

    @Transactional
    public void deletePortfolio(Long id) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "프토폴리오를 찾을 수 없습니다."));

        portfolioRepository.delete(portfolioEntity);
    }



}