package com.api.instagram.image.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "IMAGE")
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


}
