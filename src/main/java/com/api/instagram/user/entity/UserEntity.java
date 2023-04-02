package com.api.instagram.user.entity;

import com.api.instagram.image.entity.ImageEntity;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity(name = "USER")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "nickname")
    private String nickname;
    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageEntity imageId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "refresh_token")
    private String refreshToken;
}
