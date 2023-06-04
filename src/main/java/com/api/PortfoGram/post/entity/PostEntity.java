package com.api.PortfoGram.post.entity;

import com.api.PortfoGram.image.entity.ImageEntity;
import com.api.PortfoGram.post.dto.Post;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    @Column(name = "content", nullable = false)
    private String content;


    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageEntity> postImages;

    @Builder
    public PostEntity(Long id, UserEntity user, String content, Date createdAt, List<PostImageEntity> postImages) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.postImages = postImages;
    }


    public void addImage(PostImageEntity postImageEntity) {
        postImages.add(postImageEntity);
        postImageEntity.setPost(this);
    }

    public void setContent(String content) {
        this.content = content;
    }


}
