package com.api.PortfoGram.post.entity;

import com.api.PortfoGram.image.entity.ImageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post_image")
public class PostImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "image_id", nullable = false)
    private ImageEntity image;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private PostEntity post;

    @Builder
    public PostImageEntity(Long id, ImageEntity image, PostEntity post) {
        this.id = id;
        this.image = image;
        this.post = post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

}