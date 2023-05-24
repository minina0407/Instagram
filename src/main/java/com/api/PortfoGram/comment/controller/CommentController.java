package com.api.PortfoGram.comment.controller;

import com.api.PortfoGram.comment.dto.Comment;

import com.api.PortfoGram.comment.service.CommentService;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;


    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId, @Valid @RequestBody Comment comment) {
        Comment createdComment = commentService.createComment(postId, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @Valid @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(commentId, comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
