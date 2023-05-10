package com.api.instagram.comment.repository;

import com.api.instagram.comment.entity.CommentEntity;
import com.api.instagram.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository  extends JpaRepository<CommentEntity, Long> {
}
