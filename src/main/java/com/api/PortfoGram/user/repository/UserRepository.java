package com.api.PortfoGram.user.repository;

import com.api.PortfoGram.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(@Param("email") String email);
    boolean existsByEmail(@Param("email") String email);
    boolean existsByNickname(@Param("nickname") String nickname);


}
