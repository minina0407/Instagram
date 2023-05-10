package com.api.instagram.reply.dto;

import com.api.instagram.user.dto.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Reply{
    private Long id;
    private Long userId;
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    public Reply(Long id, Long userId, Long commentId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
    }


}