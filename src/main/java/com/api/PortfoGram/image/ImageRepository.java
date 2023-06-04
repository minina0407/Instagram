package com.api.PortfoGram.image;

import com.api.PortfoGram.image.entity.ImageEntity;
import com.api.PortfoGram.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
