package com.api.instagram.post.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @NotBlank(message = "제목이 입력되지 않았습니다.")
    private String title;
    @NotBlank(message = "내용이 입력되지 않았습니다.")
    private String content;
    private MultipartFile image;
    private String imageUrl;

    @Builder
    public Post(String title,MultipartFile image, String content, String imageUrl) {
        this.title = title;
        this.image = image;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }

    public void update(String content, String imageUrl) {
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
