package com.api.PortfoGram.comment.dto;

import com.api.PortfoGram.user.dto.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Comment{
    private Long id;
    private User user;
    private Long postId;
    private String content;
    private Date createdAt;

    // constructors, getters, and setters
    @Builder
    public Comment(Long id, User user, Long postId, String content, Date createdAt) {
        this.id = id;
        this.user = user;
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }



}