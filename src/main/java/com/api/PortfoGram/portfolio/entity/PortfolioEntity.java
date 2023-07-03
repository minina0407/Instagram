package com.api.PortfoGram.portfolio.entity;

import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "portfolio")
public class PortfolioEntity {
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

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioImageEntity> portfolioImages;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments ;
    @Builder
    public PortfolioEntity(Long id, UserEntity user, String content, Date createdAt, int likeCount, List<PortfolioImageEntity> portfolioImages, List<CommentEntity> comments) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.comments = comments;
        this.portfolioImages = new ArrayList<>(); // Initialize the portfolioImages list
        if (portfolioImages != null) {
            this.portfolioImages.addAll(portfolioImages);
        }
    }

    public void addImage(PortfolioImageEntity portfolioImageEntity) {
        portfolioImages.add(portfolioImageEntity);
        portfolioImageEntity.setPost(this);
    }

    public void updateContent(String content) {
        this.content = content;
    }
    public void updateImage(List<PortfolioImageEntity> postImages){
        this.portfolioImages = postImages;
    }
    public  void setLikeCount(int updatedLikeCount){
        this.likeCount = updatedLikeCount;
    }
}
