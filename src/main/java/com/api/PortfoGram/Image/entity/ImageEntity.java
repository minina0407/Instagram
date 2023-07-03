package com.api.PortfoGram.Image.entity;

import com.api.PortfoGram.Image.dto.Image;
import com.api.PortfoGram.portfolio.entity.PortfolioImageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_yn", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Integer deletedYn;

    @Column(name = "original_filename", nullable = false)
    private String originalFileName;

    @Column(name = "filesize")
    private Long fileSize;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "endpoint")
    private String endPoint;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    private PortfolioImageEntity postImage;

    @Builder
    public ImageEntity(Long imageId, LocalDateTime createdAt, LocalDateTime deletedAt, int deletedYn, String originalFileName, Long fileSize, String fileName, String endPoint, PortfolioImageEntity postImage) {
        this.imageId = imageId;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.deletedYn = deletedYn;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.endPoint = endPoint;
        this.postImage = postImage;
    }

    public static Image fromEntity(ImageEntity image) {
        return Image.builder()
                .imageId(image.getImageId())
                .fileName(image.getFileName())
                .fileSize(image.getFileSize())
                .endPoint(image.getEndPoint())
                .originalFileName(image.getOriginalFileName())
                .createdAt(image.getCreatedAt())
                .deletedAt(image.getDeletedAt())
                .deletedYn(image.getDeletedYn())
                .build();
    }


}
