package com.api.PortfoGram.comment.controller;

import com.api.PortfoGram.comment.dto.Comment;

import com.api.PortfoGram.comment.service.CommentService;

import javax.validation.Valid;

import com.api.PortfoGram.reply.dto.Reply;
import com.api.PortfoGram.reply.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;
    private final ReplyService replyService;

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<Reply>> getRepliesByCommentId(@PathVariable Long commentId,
                                                             @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Reply> replies = replyService.getRepliesByCommentId(commentId, pageable);
        return new ResponseEntity<>(replies,HttpStatus.OK);
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @Valid @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(commentId,comment);
        return new ResponseEntity<>(updatedComment,HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
