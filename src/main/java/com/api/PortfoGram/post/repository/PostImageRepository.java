package com.api.PortfoGram.post.repository;

import com.api.PortfoGram.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostEntity, Long> {
}
