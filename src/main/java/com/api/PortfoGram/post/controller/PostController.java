package com.api.PortfoGram.post.controller;

import com.api.PortfoGram.post.dto.Post;
import com.api.PortfoGram.post.service.PostService;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Post> savePost(@Valid @RequestParam(name = "image") MultipartFile image, @RequestParam(name = "content") String content) throws IOException {
        Post savedPost = postService.savePost(image, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @Valid @RequestBody Post postRequest) throws IOException {
        Post updatedPost = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
