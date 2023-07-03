package com.api.PortfoGram.post.dto;

import com.api.PortfoGram.comment.dto.Comment;
import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.image.dto.Image;
import com.api.PortfoGram.image.entity.ImageEntity;
import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.post.entity.PostImageEntity;
import com.api.PortfoGram.reply.dto.Reply;
import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post  implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    @NotBlank(message = "내용이 없습니다.")
    private String content;
    private Date createdAt;
    private List<PostImage> postImages;
    private List<Comment> comments;
    private List<Reply> replies;

    @Builder
    public Post(Long id, Long userId, String content, Date createdAt, List<PostImage> postImages, List<Comment> comments, List<Reply> replies) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.postImages = postImages;
        this.comments = comments;
        this.replies = replies;
    }

    public static Post fromEntity(PostEntity postEntity) {
        List<PostImage> postImages = postEntity.getPostImages().stream()
                .map(PostImage::fromEntity)
                .collect(Collectors.toList());

        List<Comment> comments = postEntity.getComments().stream()
                .map(Comment::fromEntity)
                .collect(Collectors.toList());


        return Post.builder()
                .id(postEntity.getId())
                .content(postEntity.getContent())
                .userId(postEntity.getUser().getId())
                .postImages(postImages)
                .comments(comments)
                .build();
    }
}


