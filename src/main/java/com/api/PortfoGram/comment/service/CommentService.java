package com.api.PortfoGram.comment.service;

import com.api.PortfoGram.comment.dto.Comment;

import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.comment.repository.CommentRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import com.api.PortfoGram.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PortfolioService portfolioService;


    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByPostId(Long portfolioId, Pageable pageable) {
        PortfolioEntity portfolioEntity = portfolioService.getPortfolioEntityId(portfolioId);
        Page<CommentEntity> commentEntitiesPage = commentRepository.findAllByPortfolio(portfolioEntity, pageable);
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
