package com.api.PortfoGram.reply.service;

import com.api.PortfoGram.comment.dto.Comment;
import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.comment.service.CommentService;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.reply.ReplyRepository;
import com.api.PortfoGram.reply.dto.Reply;
import com.api.PortfoGram.reply.entity.ReplyEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserService userService;
    private final CommentService commentService;

    @Transactional(readOnly = true)
    public Page<Reply> getRepliesByCommentId(Long commentId, Pageable pageable) {
        CommentEntity commentEntity = commentService.getCommentById(commentId);

        Page<ReplyEntity> replyEntitiesPage = replyRepository.findAllByComment(commentEntity, pageable);
        return replyEntitiesPage.map(Reply::fromEntity);

    }

    @Transactional
    public void createReply(Reply reply) {
        CommentEntity comment = commentService.getCommentById(reply.getCommentId());

        UserEntity user = userService.getMyUserWithAuthorities();

        ReplyEntity replyEntity = ReplyEntity.builder()
                .user(user)
                .comment(comment)
                .content(reply.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        replyRepository.save(replyEntity);
    }

    @Transactional
    public Reply updateReply(Long id, Reply request) {
        ReplyEntity replyEntity = replyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "답글을 찾을 수 없습니다."));

        replyEntity.setContent(request.getContent());

        ReplyEntity updatedReplyEntity = replyRepository.save(replyEntity);

        return Reply.builder()
                .id(updatedReplyEntity.getId())
                .content(updatedReplyEntity.getContent())
                .build();
    }

    @Transactional
    public void deleteReply(Long id) {
        ReplyEntity replyEntity = replyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "답글을 찾을 수 없습니다."));
        replyRepository.delete(replyEntity);
    }
}