package com.api.PortfoGram.portfolio.dto;

import com.api.PortfoGram.Image.dto.Image;
import com.api.PortfoGram.portfolio.entity.PortfolioImageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioImage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Image image;

    @Builder
    public PortfolioImage(Long id, Image image) {
        this.id = id;
        this.image = image;
    }

    public static PortfolioImage fromEntity(PortfolioImageEntity portfolioImageEntity) {
        return PortfolioImage.builder()
                .id(portfolioImageEntity.getId())
                .image(Image.fromEntity(portfolioImageEntity.getImage()))
                .build();
    }
}
