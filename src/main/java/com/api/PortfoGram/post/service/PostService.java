package com.api.PortfoGram.post.service;

import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.image.dto.Image;
import com.api.PortfoGram.image.dto.Images;
import com.api.PortfoGram.image.service.ImageService;
import com.api.PortfoGram.post.dto.Post;
import com.api.PortfoGram.post.dto.PostImage;
import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.post.entity.PostImageEntity;
import com.api.PortfoGram.post.repository.PostRepository;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final RedisTemplate redisTemplate;

    @Transactional
    public void savePost(String content, List<MultipartFile> imageFiles) {
        UserEntity user = userService.getMyUserWithAuthorities();

        PostEntity postEntity = PostEntity.builder()
                .user(user)
                .content(content)
                .build();

        // 게시물 저장
        PostEntity savedPostEntity = postRepository.save(postEntity);

        Images uploadedImages = imageService.uploadImage(imageFiles);
        List<Image> imageList = uploadedImages.getImages();
        for (Image image : imageList) {
            PostImageEntity postImageEntity = PostImageEntity.builder()
                    .image(image.toImageEntity())
                    .post(savedPostEntity)
                    .build();
            savedPostEntity.addImage(postImageEntity);
        }
    }


    @Transactional
    public void incrementLikeCount(Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"Post not found"));

        int currentLikeCount = postEntity.getLikeCount();
        int updatedLikeCount = currentLikeCount + 1;
        postEntity.setLikeCount(updatedLikeCount);

        postRepository.save(postEntity);
    }
    @Transactional
    public List<Post> getFollowedPortfolios(Long userId) {
        String redisKey = "user:" + userId + ":followedPortfolios";
        Set<String> followedPortfolioIds = redisTemplate.opsForSet().members(redisKey);

        if (followedPortfolioIds != null && !followedPortfolioIds.isEmpty()) {
            List<Long> portfolioIds = followedPortfolioIds.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<PostEntity> followedPortfolioEntities = postRepository.findAllById(portfolioIds);
            return followedPortfolioEntities.stream()
                    .map(Post::fromEntity)
                    .collect(Collectors.toList());
        }

        List<PostEntity> followedPortfolioEntities = postRepository.findFollowedPosts(userId);
        List<Post> followedPortfolios = followedPortfolioEntities.stream()
                .map(Post::fromEntity)
                .collect(Collectors.toList());

        if (!followedPortfolios.isEmpty()) {
            Set<String> updatedPortfolioIds = followedPortfolios.stream()
                    .map(postDto -> String.valueOf(postDto.getId()))
                    .collect(Collectors.toSet());

            redisTemplate.opsForSet().add(redisKey, updatedPortfolioIds.toArray(new String[0]));
        }

        return followedPortfolios;
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long feedId) {
        String redisKey = "post:" + feedId;
        Post post = (Post) redisTemplate.opsForValue().get(redisKey);

        if (post == null) {
            PostEntity postEntity = postRepository.findPostById(feedId)
                    .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "피드를 찾을 수 없습니다."));
            List<PostImage> postImages = postEntity.getPostImages()
                    .stream()
                    .map(PostImage::fromEntity)
                    .collect(Collectors.toList());

            post = Post.builder()
                    .id(postEntity.getId())
                    .userId(postEntity.getUser().getId())
                    .content(postEntity.getContent())
                    .postImages(postImages)
                    .build();

            redisTemplate.opsForValue().set(redisKey, post);
        }

        return post;
    }

    @Transactional(readOnly = true)
    public PostEntity getPoseEntityId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        List<PostEntity> postsEntity = postRepository.findAll();

        if (postsEntity.isEmpty()) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글이 없습니다.");
        }

        List<Post> posts = postsEntity.stream()
                .map(Post::fromEntity)
                .collect(Collectors.toList());

        return posts;
    }

    @Transactional
    public void updatePost(Long id, Post postRequest) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        postEntity.updateContent(postRequest.getContent()); // 게시글 내용 업데이트

        postRepository.save(postEntity);

    }

    @Transactional
    public void deletePost(Long id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        postRepository.delete(postEntity);
    }

}