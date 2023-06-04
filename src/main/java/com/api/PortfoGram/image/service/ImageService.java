package com.api.PortfoGram.image.service;

import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.image.ImageRepository;


import com.api.PortfoGram.image.dto.Image;
import com.api.PortfoGram.image.dto.Images;
import com.api.PortfoGram.image.entity.ImageEntity;
import com.api.PortfoGram.post.dto.Post;
import com.api.PortfoGram.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    @Value("${endpoint}")
    private String endpoint;

    @Transactional
    public Images uploadImage(List<MultipartFile> multipartFiles){
        if (multipartFiles == null) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_MISSING, "multipartFiles는 null일 수 없습니다.");
        }

        List<Image> imageList = multipartFiles.stream()
                .map(multipartFile -> {
                    try {
                        return uploadFile(multipartFile);
                    } catch (IOException ex) {
                        throw new BadRequestException(ExceptionEnum.RESPONSE_INTERNAL_SEVER_ERROR, "파일을 업로드하는 중 오류가 발생했습니다.");
                    }
                })
                .collect(Collectors.toList());

        return Images.builder()
                .images(imageList)
                .build();
    }

    public Image uploadFile(MultipartFile multipartFile) throws IOException {
        UUID uuid = UUID.randomUUID();
        String filename = uuid + "_" + multipartFile.getOriginalFilename();

        Image image = Image.builder()
                .originalFileName(multipartFile.getOriginalFilename())
                .fileName("/files/" + filename)
                .fileSize(multipartFile.getSize())
                .endPoint(endpoint)
                .build();

        ImageEntity imageEntity = ImageEntity.builder()
                .originalFileName(image.getOriginalFileName())
                .fileSize(image.getFileSize())
                .fileName(image.getFileName())
                .endPoint(image.getEndPoint())
                .build();

        imageRepository.save(imageEntity);
        return image;
    }
}
