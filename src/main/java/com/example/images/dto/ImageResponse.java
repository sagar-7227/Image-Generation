package com.example.images.dto;

import com.example.images.entity.Image;

public class ImageResponse {
    private Long id;
    private String url;
    private String originalName;
    private Long size;
    private Integer width;
    private Integer height;
    private String uploadDate;

    public ImageResponse(Image image) {
        this.id = image.getId();
        this.url = image.getUrl();
        this.originalName = image.getOriginalName();
        this.size = image.getSize();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.uploadDate = image.getUploadDate().toString();
    }

}
