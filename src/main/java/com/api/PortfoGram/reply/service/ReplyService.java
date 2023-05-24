package com.api.PortfoGram.reply.service;

import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.comment.repository.CommentRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.reply.ReplyRepository;
import com.api.PortfoGram.reply.dto.Reply;
import com.api.PortfoGram.reply.entity.ReplyEntity;
import com.api.PortfoGram.user.UserRepository;
import com.api.PortfoGram.user.entity.UserEntity;
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