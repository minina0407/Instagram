package com.api.PortfoGram.post.repository;

import com.api.PortfoGram.post.dto.Post;
import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Optional<PostEntity> findPostById(Long id);

    @Query("SELECT p FROM PostEntity p " +
            "INNER JOIN p.user u " +
            "INNER JOIN u.followings fu " +
            "WHERE fu.id = :userId")
    List<PostEntity> findFollowedPosts(@Param("userId") Long userId);
}