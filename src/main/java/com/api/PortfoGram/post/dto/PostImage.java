package com.api.PortfoGram.post.dto;


import com.api.PortfoGram.image.dto.Image;
import com.api.PortfoGram.post.entity.PostImageEntity;
import com.api.PortfoGram.user.dto.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostImage {
    private Long id;
    private Image image;
    @Builder
    public PostImage(Long id, Image image) {
        this.id = id;
        this.image = image;
    }


    public static PostImage fromEntity(PostImageEntity postImageEntity) {
        return PostImage.builder()
                .id(postImageEntity.getId())
                .image(Image.fromEntity(postImageEntity.getImage()))
                .build();
    }
}
