package com.api.PortfoGram.comment.service;

import com.api.PortfoGram.comment.dto.Comment;

import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.comment.repository.CommentRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;


    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByPostId(Long postId, Pageable pageable) {
        PostEntity postEntity = postService.getPoseEntityId(postId);
        Page<CommentEntity> commentEntitiesPage = commentRepository.findAllByPost(postEntity, pageable);
        return commentEntitiesPage.map(Comment::fromEntity);

    }

    @Transactional(readOnly = true)
    public CommentEntity getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "댓글을 찾을 수 없습니다."));
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment comment) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "댓글을 찾을 수 없습니다."));

        commentEntity.setContent(comment.getContent());
        CommentEntity updatedCommentEntity = commentRepository.save(commentEntity);

        Comment updatedComment = Comment.builder()
                .id(updatedCommentEntity.getId())
                .content(updatedCommentEntity.getContent())
                .build();

        return updatedComment;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "댓글을 찾을 수 없습니다."));

        commentRepository.delete(commentEntity);
    }

}
