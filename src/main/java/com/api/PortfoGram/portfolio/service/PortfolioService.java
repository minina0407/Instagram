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
    public void savePost(String content, List<MultipartFile> imageFiles) {
        UserEntity user = userService.getMyUserWithAuthorities();

        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .user(user)
                .content(content)
                .build();

        // 게시물 저장
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
    }


    @Transactional
    public void incrementLikeCount(Long postId) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"Portfolio not found"));

        int currentLikeCount = portfolioEntity.getLikeCount();
        int updatedLikeCount = currentLikeCount + 1;
        portfolioEntity.setLikeCount(updatedLikeCount);

        portfolioRepository.save(portfolioEntity);
    }
    @Transactional
    public List<Portfolio> getFollowedPortfolios(Long userId) {
        String redisKey = "user:" + userId + ":followedPortfolios";
        Set<String> followedPortfolioIds = redisTemplate.opsForSet().members(redisKey);

        if (followedPortfolioIds != null && !followedPortfolioIds.isEmpty()) {
            List<Long> portfolioIds = followedPortfolioIds.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<PortfolioEntity> followedPortfolioEntities = portfolioRepository.findAllById(portfolioIds);
            return followedPortfolioEntities.stream()
                    .map(Portfolio::fromEntity)
                    .collect(Collectors.toList());
        }

        List<PortfolioEntity> followedPortfolioEntities = portfolioRepository.findFollowedPortfolios(userId);
        List<Portfolio> followedPortfolios = followedPortfolioEntities.stream()
                .map(Portfolio::fromEntity)
                .collect(Collectors.toList());

        if (!followedPortfolios.isEmpty()) {
            Set<String> updatedPortfolioIds = followedPortfolios.stream()
                    .map(portfolioDto -> String.valueOf(portfolioDto.getId()))
                    .collect(Collectors.toSet());

            redisTemplate.opsForSet().add(redisKey, updatedPortfolioIds.toArray(new String[0]));
        }

        return followedPortfolios;
    }

    @Transactional(readOnly = true)
    public Portfolio getPostById(Long feedId) {
        String redisKey = "portfolio:" + feedId;
        Portfolio portfolio = (Portfolio) redisTemplate.opsForValue().get(redisKey);

        if (portfolio == null) {
            PortfolioEntity portfolioEntity = portfolioRepository.findPostById(feedId)
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
    public PortfolioEntity getPoseEntityId(Long postId) {
        return portfolioRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

    }

    @Transactional(readOnly = true)
    public List<Portfolio> getAllPosts() {
        List<PortfolioEntity> postsEntity = portfolioRepository.findAll();

        if (postsEntity.isEmpty()) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글이 없습니다.");
        }

        List<Portfolio> portfolios = postsEntity.stream()
                .map(Portfolio::fromEntity)
                .collect(Collectors.toList());

        return portfolios;
    }

    @Transactional
    public void updatePost(Long id, Portfolio portfolioRequest) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        portfolioEntity.updateContent(portfolioRequest.getContent()); // 게시글 내용 업데이트

        portfolioRepository.save(portfolioEntity);

    }

    @Transactional
    public void deletePost(Long id) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        portfolioRepository.delete(portfolioEntity);
    }

}