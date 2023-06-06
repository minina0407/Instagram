package com.api.PortfoGram.user.entity;
import com.api.PortfoGram.image.entity.ImageEntity;
import com.api.PortfoGram.user.dto.UserImage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private ImageEntity image;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder
    public UserImageEntity(Long id, ImageEntity image, UserEntity user) {
        this.id = id;
        this.image = image;
        this.user = user;
    }

    public void setUser(UserEntity userEntity){
        this.user = userEntity;
    }
}
