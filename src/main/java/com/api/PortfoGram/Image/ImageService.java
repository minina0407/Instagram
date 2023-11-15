package com.api.PortfoGram.Image;

import com.api.PortfoGram.Image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public String upload(MultipartFile file) {
      String newFileName = UUID.randomUUID().toString()+"_"+file.getOriginalFilename();
        String uploadDir = "C:\\ProtfoGram\\PortfoGram\\src\\main\\resources\\static\\images\\";
        File uploadFile = new File(uploadDir + newFileName);
      try {
          file.transferTo(uploadFile);
      } catch (IOException e) {
        e.printStackTrace();
      }

      return newFileName;
    }



}
