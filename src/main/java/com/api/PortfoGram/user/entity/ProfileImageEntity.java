package com.api.PortfoGram.user.entity;
import com.api.PortfoGram.Image.entity.ImageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "profile_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "image_id", nullable = false)
    private ImageEntity image;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Builder
    public ProfileImageEntity(Long id, ImageEntity image, UserEntity user) {
        this.id = id;
        this.image = image;
        this.user = user;
    }
}
