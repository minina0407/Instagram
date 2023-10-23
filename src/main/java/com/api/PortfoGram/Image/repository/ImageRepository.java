package com.api.PortfoGram.Image.repository;

import com.api.PortfoGram.Image.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    public Optional< ImageEntity> findByImageId(Long imageId);
}
