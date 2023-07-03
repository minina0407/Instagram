package com.api.PortfoGram.comment.repository;

import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CommentRepository  extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByPortfolio(@Param("portfolio") PortfolioEntity portfolio, Pageable pageable);

}
