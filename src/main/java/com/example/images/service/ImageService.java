package com.example.images.service;

import com.example.images.dto.ImageResponse;
import com.example.images.dto.TransformationRequest;
import com.example.images.entity.Image;
import com.example.images.entity.User;
import com.example.images.exception.CustomException;
import com.example.images.repository.ImageRepository;
import com.example.images.util.ImageProcessorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserService userService;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.max-file-size}")
    private long maxFileSize;

    private final ConcurrentHashMap<String, Integer> rateLimiter = new ConcurrentHashMap<>();

    public Image uploadImage(MultipartFile file) {
        User user = userService.getCurrentUser();

        if (file.isEmpty() || file.getSize() > maxFileSize) {
            throw new CustomException("Invalid file: empty or exceeds size limit");
        }

        String userKey = user.getId().toString();
        rateLimiter.compute(userKey + "_upload", (k, v) -> v == null ? 1 : v + 1);
        if (rateLimiter.get(userKey + "_upload") > 10) {
            throw new CustomException("Upload rate limit exceeded");
        }

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String fileName = UUID.randomUUID() + extension;

            Path filePath = Paths.get(uploadDir, fileName);
            file.transferTo(filePath);

            int[] dimensions = ImageProcessorUtil.getImageDimensions(filePath.toString());
            String mimeType = Files.probeContentType(filePath);

            Image image = new Image();
            image.setOriginalName(originalName);
            image.setFileName(fileName);
            image.setUrl("/uploads/" + fileName);
            image.setMimeType(mimeType);
            image.setSize(file.getSize());
            image.setWidth(dimensions[0]);
            image.setHeight(dimensions[1]);
            image.setUser(user);

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new CustomException("Failed to upload image: " + e.getMessage());
        }
    }

    @CacheEvict(value = "transformedImages", key = "#imageId")
    public Image transformImage(Long imageId, TransformationRequest request) {
        Image original = imageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException("Image not found"));

        User user = userService.getCurrentUser();
        if (!original.getUser().equals(user)) {
            throw new CustomException("Unauthorized access to image");
        }

        String userKey = user.getId().toString();
        rateLimiter.compute(userKey + "_transform", (k, v) -> v == null ? 1 : v + 1);
        if (rateLimiter.get(userKey + "_transform") > 5) {
            throw new CustomException("Transformation rate limit exceeded");
        }

        try {
            Path originalPath = Paths.get(uploadDir, original.getFileName());
            Path transformedPath = ImageProcessorUtil.applyTransformations(originalPath, request.getTransformations());

            int[] dims = ImageProcessorUtil.getImageDimensions(transformedPath.toString());
            String mimeType = Files.probeContentType(transformedPath);

            Image transformed = new Image();
            transformed.setOriginalName(original.getOriginalName() + "_transformed");
            transformed.setFileName(transformedPath.getFileName().toString());
            transformed.setUrl("/uploads/" + transformedPath.getFileName());
            transformed.setMimeType(mimeType);
            transformed.setSize(Files.size(transformedPath));
            transformed.setWidth(dims[0]);
            transformed.setHeight(dims[1]);
            transformed.setUser(user);

            return imageRepository.save(transformed);
        } catch (IOException e) {
            throw new CustomException("Failed to transform image: " + e.getMessage());
        }
    }

    @Cacheable(value = "images", key = "#id")
    public byte[] retrieveImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new CustomException("Image not found"));

        User user = userService.getCurrentUser();
        if (!image.getUser().equals(user)) {
            throw new CustomException("Unauthorized access");
        }

        try {
            Path path = Paths.get(uploadDir, image.getFileName());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new CustomException("Failed to read image: " + e.getMessage());
        }
    }

    public Page<ImageResponse> listImages(int page, int size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Image> images = imageRepository.findByUser(user, pageable);
        return images.map(ImageResponse::new);
    }
}