package com.api.instagram.image.repository;

import com.api.instagram.image.dto.Image;
import com.api.instagram.image.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity,Long> {

}
