package com.api.instagram.post.service;

import com.api.instagram.exception.dto.BadRequestException;
import com.api.instagram.exception.dto.ExceptionEnum;

import com.api.instagram.image.ImageSerive;
import com.api.instagram.post.PostRepository;
import com.api.instagram.post.dto.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ImageSerive imageSerive;

    @Transactional
    public void savePost(Post postRequest) {
        Post post = postRequest.toEntity();
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public Post updatePost(Long id,Post postRequest) throws IOException {
        Post post = postRepository.findById(id).orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        String imageUrl = null;
        if (postRequest.getImage() != null) {
            imageUrl = imageSerive.saveFile(postRequest.getImage());
        }
        post.update(post.getContent(),imageUrl);
        postRepository.save(post);

        return post;}

        @Transactional
        public void deletePost (Long id){
            postRepository.deleteById(id);
        }
    }



