package com.api.instagram.reply.service;

import com.api.instagram.comment.entity.CommentEntity;
import com.api.instagram.comment.repository.CommentRepository;
import com.api.instagram.exception.dto.BadRequestException;
import com.api.instagram.exception.dto.ExceptionEnum;
import com.api.instagram.reply.ReplyRepository;
import com.api.instagram.reply.dto.Reply;
import com.api.instagram.reply.entity.ReplyEntity;
import com.api.instagram.user.UserRepository;
import com.api.instagram.user.dto.User;
import com.api.instagram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reply createReply(Reply request) {
        CommentEntity comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new BadRequestException("Comment not found"));

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        ReplyEntity replyEntity = ReplyEntity.builder()
                .user(user)
                .comment(comment)
                .content(request.getContent())
                .createdAt(new Date())
                .build();

        ReplyEntity savedReplyEntity = replyRepository.save(replyEntity);

        return Reply.builder()
                .id(savedReplyEntity.getId())
                .content(savedReplyEntity.getContent())
                .build();
    }

    @Transactional
    public Reply updateReply(Long id, Reply request) {
        ReplyEntity replyEntity = replyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Reply not found"));

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
                .orElseThrow(() -> new BadRequestException("Reply not found"));

        replyRepository.delete(replyEntity);
    }
}