package com.api.PortfoGram.portfolio.entity;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Entity
@Table(name = "portfolio_like")
public class PortfolioLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortfolioEntity portfolio;

    @Builder
    public PortfolioLikeEntity(Long id, UserEntity user, PortfolioEntity portfolio) {
        this.id = id;
        this.user = user;
        this.portfolio = portfolio;
    }

}
