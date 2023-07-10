package com.api.PortfoGram.portfolio.repository;

import com.api.PortfoGram.portfolio.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {
    Optional<PortfolioEntity> findPostById(Long id);
    @Query("SELECT p FROM PortfolioEntity p " +
            "INNER JOIN p.user u " +
            "INNER JOIN u.followings f " +
            "WHERE f.follower.id = :userId")
    List<PortfolioEntity> findFollowedPortfolios(@Param("userId") Long userId);

    //@Query("SELECT p FROM PortfolioEntity p WHERE p.id IN (:portfolioIds) ORDER BY p.createdAt DESC")
    List<PortfolioEntity> findByIdInOrderByCreatedAtDesc(@Param("portfolioIds") List<Long> portfolioIds);

    //
    // @Query("SELECT p FROM PortfolioEntity p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<PortfolioEntity> findLatestPortfoliosByUserId(@Param("userId") Long userId);

}

