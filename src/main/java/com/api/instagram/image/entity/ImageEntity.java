package com.api.instagram.image.entity;


import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "IMAGE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "original_filename")
    private String originalFileName;
    @Column(name = "filesize")
    private  Long fileSize;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "endpoint")
    private String endPoint;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public ImageEntity(Long id, String originalFileName, Long fileSize, String fileName, String endPoint, LocalDateTime createdAt) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.endPoint = endPoint;
        this.createdAt = createdAt;
    }

}
