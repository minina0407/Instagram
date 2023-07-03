package com.api.PortfoGram.post.controller;

import com.api.PortfoGram.comment.dto.Comment;
import com.api.PortfoGram.comment.service.CommentService;
import com.api.PortfoGram.post.dto.Post;
import com.api.PortfoGram.post.dto.PostLike;
import com.api.PortfoGram.post.service.PostLikeService;
import com.api.PortfoGram.post.service.PostService;

import javax.validation.Valid;

import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final CommentService commentService;
    private final PostLikeService postLikeService;


    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<Post>> getFollowedPortfolios(@PathVariable Long userId) {
        List<Post> followedPortfolios = postService.getFollowedPortfolios(userId);
        return ResponseEntity.ok(followedPortfolios);
    }
     @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<Comment>> getCommentsByPostId(@PathVariable Long postId,
                                                             @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Comment> comments = commentService.getCommentsByPostId(postId, pageable);
        return new ResponseEntity<>(comments,HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> savePost(
            @RequestParam(value = "images", required = true) List<MultipartFile> imageFiles,
            @RequestParam(value = "content", required = true) String content) {

        postService.savePost(content, imageFiles);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostLike> likePost(@PathVariable Long postId) {
         postLikeService.likePost(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @Valid @RequestBody Post post) throws IOException {
        postService.updatePost(id, post);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
