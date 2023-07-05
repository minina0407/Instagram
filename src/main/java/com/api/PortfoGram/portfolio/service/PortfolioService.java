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
            updateFollowedPortfoliosInRedis( followedPortfolios, redisKey);
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