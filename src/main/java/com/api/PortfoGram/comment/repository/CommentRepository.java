package com.api.PortfoGram.comment.repository;

import com.api.PortfoGram.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository  extends JpaRepository<CommentEntity, Long> {
}
