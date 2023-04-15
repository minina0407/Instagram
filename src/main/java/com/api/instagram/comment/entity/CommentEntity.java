package com.api.instagram.comment.entity;

import com.api.instagram.post.entity.PostEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity(name = "COMMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity postId;
    @Builder
    public CommentEntity(Long id, String content, PostEntity postId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
    }

}
