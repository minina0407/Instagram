package com.api.instagram.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Follow{
    private Long id;
    private User follower;
    private User following;
    private Date createdAt;

    // constructors, getters, and setters
}