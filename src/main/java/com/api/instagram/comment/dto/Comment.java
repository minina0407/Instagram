package com.api.instagram.comment.dto;

import com.api.instagram.post.dto.Post;
import com.api.instagram.user.dto.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Comment{
    private Long id;
    private User user;
    private Post post;
    private String content;
    private Date createdAt;

    // constructors, getters, and setters
}