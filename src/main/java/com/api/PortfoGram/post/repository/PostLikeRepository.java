package com.api.PortfoGram.post.repository;

import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.post.entity.PostLikeEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity,Long> {
    boolean existsByUserIdAndPostId(@Param("userId")Long userId,@Param("postId")Long postId);
}

