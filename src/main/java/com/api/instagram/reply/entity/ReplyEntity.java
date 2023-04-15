package com.api.instagram.reply.entity;

import com.api.instagram.comment.entity.CommentEntity;

import javax.persistence.*;

@Entity(name = "REPLY")
public class ReplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentEntity commentId;
}
