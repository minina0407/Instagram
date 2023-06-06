package com.api.PortfoGram.user.dto;

import com.api.PortfoGram.image.dto.Image;
import com.api.PortfoGram.image.dto.Images;
import com.api.PortfoGram.image.entity.ImageEntity;
import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.entity.UserImageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class UserImage {
    private Long id;
    private Image image;
    @Builder
    public UserImage(Long id, Image image) {
        this.id = id;
        this.image = image;
    }
    public static  UserImageEntity toEntity(UserImage userImage) {
        return UserImageEntity.builder()
                .id(userImage.getId())
                .image(userImage.getImage().toImageEntity())
                .build();
    }

    public static  UserImage fromEntity(UserImageEntity userImageEntity) {
        return UserImage.builder()
                .id(userImageEntity.getId())
                .image(Image.fromEntity(userImageEntity.getImage()))
                .build();
    }

}
