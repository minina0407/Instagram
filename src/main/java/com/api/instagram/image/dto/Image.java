package com.api.instagram.image.dto;

import com.api.instagram.image.entity.ImageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Image {
    private Long id;
    private String originalFileName;
    private Long fileSize;
    private String fileName;
    private String endPoint;
    @Builder
    public Image(Long id, String originalFileName, Long fileSize, String fileName, String endPoint) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.endPoint = endPoint;
    }
    public static ImageEntity toEntity(Image image){
        return ImageEntity.builder()
                .id(image.getId())
                .fileName(image.fileName)
                .originalFileName(image.originalFileName)
                .endPoint(image.endPoint)
                .fileSize(image.fileSize)
                .build();
    }
    public static Image fromEntity(ImageEntity image){
        return Image.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .fileSize(image.getFileSize())
                .endPoint(image.getEndPoint())
                .originalFileName(image.getOriginalFileName())
                .build();
    }

}
