package com.api.PortfoGram.portfolio.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long postId;
    private Long userId;
    @Builder
    public PostLike(Long id, Long postId, Long userId) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
    }
}
