package com.api.PortfoGram.comment.controller;

import com.api.PortfoGram.comment.dto.Comment;

import com.api.PortfoGram.comment.service.CommentService;

import javax.validation.Valid;

import com.api.PortfoGram.reply.dto.Reply;
import com.api.PortfoGram.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "댓글 API", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;
    private final ReplyService replyService;

    @GetMapping("/{commentId}/replies")
    @Operation(summary = "댓글의 답변 조회", description = "특정 댓글에 대한 답변을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답변 조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    public ResponseEntity<Page<Reply>> getRepliesByCommentId(
            @PathVariable("commentId") Long commentId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Reply> replies = replyService.getRepliesByCommentId(commentId, pageable);
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    public ResponseEntity<Comment> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody Comment comment
    ) {
        Comment updatedComment = commentService.updateComment(commentId, comment);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
