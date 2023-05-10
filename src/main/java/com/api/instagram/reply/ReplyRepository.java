package com.api.instagram.reply;

import com.api.instagram.comment.entity.CommentEntity;
import com.api.instagram.reply.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
}
