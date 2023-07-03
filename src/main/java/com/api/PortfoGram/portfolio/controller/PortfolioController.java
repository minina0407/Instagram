package com.api.PortfoGram.portfolio.controller;

import com.api.PortfoGram.comment.dto.Comment;
import com.api.PortfoGram.comment.service.CommentService;
import com.api.PortfoGram.portfolio.dto.Portfolio;
import com.api.PortfoGram.portfolio.dto.PostLike;
import com.api.PortfoGram.portfolio.service.PortfolioService;
import com.api.PortfoGram.portfolio.service.PortfolioLikeService;

import javax.validation.Valid;

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
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final CommentService commentService;
    private final PortfolioLikeService postLikeService;


    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long id) {
        Portfolio portfolio = portfolioService.getPostById(id);
        return new ResponseEntity<>(portfolio, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        List<Portfolio> portfolios = portfolioService.getAllPosts();
        return new ResponseEntity<>(portfolios, HttpStatus.OK);
    }
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<Portfolio>> getFollowedPortfolios(@PathVariable Long userId) {
        List<Portfolio> followedPortfolios = portfolioService.getFollowedPortfolios(userId);
        return ResponseEntity.ok(followedPortfolios);
    }
     @GetMapping("/{portfolio}/comments")
    public ResponseEntity<Page<Comment>> getCommentsByPortfolioId(@PathVariable Long portfolioId,
                                                                  @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Comment> comments = commentService.getCommentsByPostId(portfolioId, pageable);
        return new ResponseEntity<>(comments,HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Portfolio> savePortfolio(
            @RequestParam(value = "images", required = true) List<MultipartFile> imageFiles,
            @RequestParam(value = "content", required = true) String content) {

        portfolioService.savePost(content, imageFiles);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/{portfolioId}/likes")
    public ResponseEntity<PostLike> likePortfolio(@PathVariable Long portfolioId) {
         postLikeService.likePortfolio(portfolioId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Long id, @Valid @RequestBody Portfolio portfolio) throws IOException {
        portfolioService.updatePost(id, portfolio);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
