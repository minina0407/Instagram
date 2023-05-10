package com.api.instagram.post.service;

import com.api.instagram.exception.dto.BadRequestException;
import com.api.instagram.exception.dto.ExceptionEnum;
import com.api.instagram.post.PostRepository;
import com.api.instagram.post.dto.Post;
import com.api.instagram.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final S3Service s3Service;

    @Transactional
    public Post savePost(MultipartFile image, String content) throws IOException {
        String imageUrl = null;
        if (image != null) {
            imageUrl = s3Service.upload(image);
        }

        PostEntity postEntity = PostEntity.builder()
                .content(content)
                .imageUrl(imageUrl)
                .build();

        PostEntity savedPostEntity = postRepository.save(postEntity);

        return Post.builder()
                .id(savedPostEntity.getId())
                .content(savedPostEntity.getContent())
                .imageUrl(savedPostEntity.getImageUrl())
                .build();
    }
    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Post not found"));

        return Post.fromPostEntity(post);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        List<PostEntity> posts = postRepository.findAll();
        return posts.stream()
                .map(Post::fromPostEntity)
                .collect(Collectors.toList());
    }
    @Transactional
    public Post updatePost(Long id, Post postRequest) throws IOException {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        postEntity.setContent(postRequest.getContent());

        PostEntity updatedPostEntity = postRepository.save(postEntity);

        return Post.builder()
                .id(updatedPostEntity.getId())
                .content(updatedPostEntity.getContent())
                .imageUrl(updatedPostEntity.getImageUrl())
                .build();
    }

    @Transactional
    public void deletePost(Long id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));
        postRepository.delete(postEntity);
    }
}