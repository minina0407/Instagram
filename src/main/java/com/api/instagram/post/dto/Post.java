package com.api.instagram.post.dto;

import com.api.instagram.post.entity.PostEntity;
import com.api.instagram.user.dto.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;



@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Post{
    private Long id;
    private User user;
    private String content;
    private String imageUrl;
    private Date createdAt;

    @Builder
    public Post(Long id, User user, String content, String imageUrl, Date createdAt) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public static Post fromPostEntity(PostEntity postEntity) {
        if (postEntity == null) {
            return null;
        }
        return Post.builder()
                .id(postEntity.getId())
                .content(postEntity.getContent())
                .imageUrl(postEntity.getImageUrl())
                .build();
    }
}


