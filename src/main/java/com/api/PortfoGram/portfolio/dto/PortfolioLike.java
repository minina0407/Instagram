package com.api.PortfoGram.portfolio.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioLike implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long portfolioId;
    private Long userId;
    @Builder
    public PortfolioLike(Long id, Long portfolioId, Long userId) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.userId = userId;
    }
}
