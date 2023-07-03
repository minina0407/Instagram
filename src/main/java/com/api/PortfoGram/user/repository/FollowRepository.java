package com.api.PortfoGram.user.repository;

import com.api.PortfoGram.user.entity.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long > {
}
