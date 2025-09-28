package com.example.images.dto;

import com.example.images.entity.Image;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private Long id;
    private String fileName;
    private String originalName;
    private String url;
    private Long size;
    private Integer width;
    private Integer height;
    private String mimeType;
    private LocalDateTime uploadDate;

    public ImageResponse(Image image) {
        this.id = image.getId();
        this.fileName = image.getFileName();
        this.originalName = image.getOriginalName();
        this.url = image.getUrl();
        this.size = image.getSize();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.mimeType = image.getMimeType();
        this.uploadDate = image.getUploadDate();
    }
}