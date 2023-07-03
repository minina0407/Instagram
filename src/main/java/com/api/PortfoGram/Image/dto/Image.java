package com.api.PortfoGram.Image.dto;

import com.api.PortfoGram.Image.entity.ImageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long imageId;
    private String originalFileName;
    private Long fileSize;
    private String fileName;
    private String endPoint;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private int deletedYn;


    @Builder
    public Image(Long imageId, String originalFileName, Long fileSize, String fileName, String endPoint,
                 LocalDateTime createdAt, LocalDateTime deletedAt, int deletedYn) {
        this.imageId = imageId;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.endPoint = endPoint;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.deletedYn = deletedYn;
    }
    public ImageEntity toImageEntity() {
        return ImageEntity.builder()
                .imageId(this.getImageId())
                .fileName(this.getFileName())
                .fileSize(this.getFileSize())
                .endPoint(this.getEndPoint())
                .originalFileName(this.getOriginalFileName())
                .createdAt(this.getCreatedAt())
                .deletedAt(this.getDeletedAt())
                .deletedYn(this.getDeletedYn())
                .build();
    }

    public static Image fromEntity(ImageEntity imageEntity) {
        return Image.builder()
                .imageId(imageEntity.getImageId())
                .createdAt(imageEntity.getCreatedAt())
                .deletedAt(imageEntity.getDeletedAt())
                .deletedYn(imageEntity.getDeletedYn())
                .originalFileName(imageEntity.getOriginalFileName())
                .fileSize(imageEntity.getFileSize())
                .fileName(imageEntity.getFileName())
                .endPoint(imageEntity.getEndPoint())
                .build();
    }

}
