package com.example.images.repository;

import com.example.images.entity.Image;
import com.example.images.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Page<Image> findByUser (User user, Pageable pageable);
}
