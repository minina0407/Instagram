package com.api.instagram.comment.entity;

import com.api.instagram.post.entity.PostEntity;
import jakarta.persistence.*;

@Entity(name = "COMMENT")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity postId;
}
