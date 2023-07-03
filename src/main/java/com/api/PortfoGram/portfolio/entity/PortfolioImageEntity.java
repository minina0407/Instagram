package com.api.PortfoGram.portfolio.entity;

import com.api.PortfoGram.Image.entity.ImageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "portfolio_image")
public class PortfolioImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "image_id", nullable = false)
    private ImageEntity image;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", referencedColumnName = "id", nullable = false)
    private PortfolioEntity portfolio;

    @Builder
    public PortfolioImageEntity(Long id, ImageEntity image, PortfolioEntity portfolio) {
        this.id = id;
        this.image = image;
        this.portfolio = portfolio;
    }

    public void setPost(PortfolioEntity post) {
        this.portfolio = post;
    }
}
