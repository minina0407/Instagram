package com.api.PortfoGram.Image.service;

import com.api.PortfoGram.Image.dto.Image;
import com.api.PortfoGram.Image.repository.ImageRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;


import com.api.PortfoGram.Image.dto.Images;
import com.api.PortfoGram.Image.entity.ImageEntity;
import com.api.PortfoGram.auth.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PortfolioImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private FileUtil fileUtil;
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
        UUID saveName = UUID.randomUUID();
        String extension = "." + Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];
        String finalFilePath =saveName + extension;

        fileUtil.saveFile(multipartFile, saveName + extension);

        Image image = Image.builder()
                .originalFileName(multipartFile.getOriginalFilename())
                .fileName(saveName + extension)
                .fileSize(multipartFile.getSize())
                .uploadPath(finalFilePath)
                .extension(extension)
                .build();

        ImageEntity imageEntity = ImageEntity.builder()
                .originalFileName(image.getOriginalFileName())
                .fileSize(image.getFileSize())
                .fileName(image.getFileName())
                .uploadPath(image.getUploadPath())
                .endPoint(image.getEndPoint())
                .build();

        imageRepository.save(imageEntity);

        return image;
    }


    public byte[] getByName(Long Id) {
        Optional<ImageEntity> optionalImage = imageRepository.findByImageId(Id);

        if (optionalImage.isEmpty())
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND);

        return fileUtil.getFile(optionalImage.get());
    }

}
