package com.api.instagram.reply.dto;

import lombok.AccessLevel;
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

}