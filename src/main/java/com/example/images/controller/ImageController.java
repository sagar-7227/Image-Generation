package com.example.images.controller;

import com.example.images.dto.ImageResponse;
import com.example.images.dto.TransformationRequest;
import com.example.images.entity.Image;
import com.example.images.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@PreAuthorize("isAuthenticated()")  // All endpoints require auth
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        Image image = imageService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ImageResponse(image));
    }

    @PostMapping("/{id}/transform")
    public ResponseEntity<ImageResponse> transformImage(@PathVariable Long id,
                                                        @Valid @RequestBody TransformationRequest request) {
        Image transformed = imageService.transformImage(id, request);
        return ResponseEntity.ok(new ImageResponse(transformed));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> retrieveImage(@PathVariable Long id) {
        byte[] imageBytes = imageService.retrieveImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);  // Default; detect actual type in prod
        headers.setContentLength(imageBytes.length);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ImageResponse>> listImages(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int limit) {
        Page<ImageResponse> images = imageService.listImages(page, limit);
        return ResponseEntity.ok(images);
    }
}
