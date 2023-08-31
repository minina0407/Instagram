package com.api.PortfoGram.comment.dto;

import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.reply.dto.Reply;
import com.api.PortfoGram.user.dto.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private User user;
    private Long portfolioId;
    private String content;
    private Date createdAt;
    private String nickname;
    private List<Reply> replies;

    @Builder
    public Comment(Long id, User user, Long portfolioId, String content, Date createdAt, String nickname, List<Reply> replies) {
        this.id = id;
        this.user = user;
        this.portfolioId = portfolioId;
        this.content = content;
        this.createdAt = createdAt;
        this.nickname = nickname;
        this.replies =replies;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public static Comment fromEntity(CommentEntity commentEntity) {

        List<Reply> replies = commentEntity.getReplies().stream()
                .map(Reply::fromEntity)
                .collect(Collectors.toList());


        return Comment.builder()
                .id(commentEntity.getId())
                .content(commentEntity.getContent())
                .nickname(commentEntity.getUser().getNickname())
                .replies(replies)
                .build();

    }

}