package com.api.PortfoGram.portfolio.dto;

import com.api.PortfoGram.comment.dto.Comment;
import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import com.api.PortfoGram.portfolio.entity.PortfolioImageEntity;
import com.api.PortfoGram.reply.dto.Reply;
import com.api.PortfoGram.user.dto.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio implements Serializable {
    private static final long serialVersionUID = 12423L;
    private Long id;
    private Long userId;
    @NotBlank(message = "내용이 없습니다.")
    private String content;
    private Date createdAt;
    private List<PortfolioImage> portfolioImages;
    private List<Comment> comments;
    private List<Reply> replies;

    @Builder
    public Portfolio(Long id, Long userId, String content, Date createdAt, List<PortfolioImage> portfolioImages, List<Comment> comments, List<Reply> replies) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.portfolioImages = portfolioImages;
        this.comments = comments;
        this.replies = replies;
    }

    public static Portfolio fromEntity(PortfolioEntity portfolioEntity) {
        List<PortfolioImage> portfolioImageList = portfolioEntity.getPortfolioImages().stream()
                .map(PortfolioImage::fromEntity)
                .collect(Collectors.toList());

        List<Comment> comments = portfolioEntity.getComments().stream()
                .map(Comment::fromEntity)
                .collect(Collectors.toList());


        return Portfolio.builder()
                .id(portfolioEntity.getId())
                .content(portfolioEntity.getContent())
                .userId(portfolioEntity.getUser().getId())
                .portfolioImages(portfolioImageList)
                .comments(comments)
                .build();
    }
}


