package com.api.instagram.post.entity;

import com.api.instagram.image.entity.ImageEntity;
import com.api.instagram.user.entity.UserEntity;
import jakarta.persistence.*;

@Entity(name = "POST")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageEntity imageId;

}
