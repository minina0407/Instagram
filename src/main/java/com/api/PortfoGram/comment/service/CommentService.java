package com.api.PortfoGram.comment.service;

import com.api.PortfoGram.comment.dto.Comment;

import com.api.PortfoGram.comment.dto.Comments;
import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.comment.repository.CommentRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import com.api.PortfoGram.portfolio.service.PortfolioService;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class CommentService {
    private final CommentRepository commentRepository;
    private final PortfolioService portfolioService;
    private final UserService userService;


    @Transactional(readOnly = true)
    public Comments getCommentsByPostId(Long portfolioId, Pageable pageable) {
       Page <CommentEntity> commentEntitiesPage = commentRepository.findAllByPortfolio(portfolioService.getPortfolioEntityId(portfolioId), pageable);

       if(commentEntitiesPage.isEmpty())
           throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "댓글이 없습니다."  );

       List<Comment> commentList = commentEntitiesPage.stream().map(Comment::fromEntity).collect(Collectors.toList());
       Comments comments = Comments.builder()
                .comments(commentList)
                .totalPages(commentEntitiesPage.getTotalPages())
                .totalElements(commentEntitiesPage.getNumberOfElements())
                .total(commentEntitiesPage.getTotalElements())
                .build();


        return comments;

    }

    @Transactional(readOnly = true)
    public CommentEntity getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "댓글을 찾을 수 없습니다."));
    }

    @Transactional
    public void  createComment(Comment comment, Long portfolioId) {
        UserEntity user = userService.getMyUserWithAuthorities();

        PortfolioEntity portfolioEntity = portfolioService.getPortfolioEntityId(portfolioId);

        CommentEntity commentEntity = CommentEntity.builder()
                .portfolio(portfolioEntity)
                .content(comment.getContent())
                .user(user)
                .build();

        commentRepository.save(commentEntity);
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
