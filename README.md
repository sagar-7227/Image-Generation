# 🖼️ Image Processing Service

A backend service built with **Spring Boot**, **MySQL**, and **Redis** that allows users to:

- 🔐 Register and log in using JWT authentication
- 📤 Upload images
- 🛠️ Apply various transformations (resize, crop, rotate, grayscale, sepia, format conversion)
- 📥 Retrieve and list images
- ⚡ Cache image data with Redis for performance

---

## 📦 Tech Stack

- **Java 17+**
- **Spring Boot 3**
    - Spring Web
    - Spring Security (JWT)
    - Spring Data JPA (MySQL)
    - Spring Data Redis (Cache)
- **MySQL** for persistent storage
- **Redis** for caching
- **Lombok** for boilerplate reduction
- **JJWT** for JWT token handling
- **Docker** (optional)

---

## ⚙️ Features

- ✅ User Authentication with JWT (Register & Login)
- ✅ Image Upload (multipart)
- ✅ Image Transformations:
    - Resize
    - Crop
    - Rotate
    - Grayscale / Sepia
    - Format Conversion (JPEG/PNG)
- ✅ Retrieve image bytes
- ✅ List all user images
- ✅ Redis-based caching
- ✅ File storage on disk (`uploads/` folder)

---

## 📁 Project Structure

com.example.images

├── config

│ ├── SecurityConfig.java
│ ├── CacheConfig.java
│ ├── WebConfig.java

├── controller

│ ├── AuthController.java
│ ├── ImageController.java

├── dto

│ ├── RegisterRequest.java
│ ├── LoginRequest.java
│ ├── ImageResponse.java
│ ├── TransformationRequest.java

├── entity

│ ├── User.java
│ ├── Image.java

├── repository

│ ├── UserRepository.java
│ ├── ImageRepository.java

├── security

│ ├── JwtUtil.java
│ ├── JwtAuthenticationFilter.java
│ ├── UserDetailsServiceImpl.java

├── service

│ ├── AuthService.java
│ ├── ImageService.java
│ ├── UserService.java

├── util

│ └── ImageProcessorUtil.java
└── ImageServiceApplication.java

---

## 🧰 Setup Instructions

### 1️⃣ Clone the repository
```bash
git clone https://github.com/sagar-7227/Image-Generation.git
cd image-processing-service

-------

🔑 Authentication Endpoints
🔹 Register

POST /auth/register

{
  "username": "user1",
  "password": "password123"
}


Response:

{
  "id": 1,
  "username": "user1"
}

🔹 Login

POST /auth/login

{
  "username": "user1",
  "password": "password123"
}


Response:

<JWT Token>


Use this token in all subsequent requests:

Authorization: Bearer <token>

🖼️ Image Endpoints
🔹 Upload Image

POST /images
Headers:

Authorization: Bearer <token>
Content-Type: multipart/form-data


Form-Data:

Key	Type	Description
file	File	Image file

Response:

{
  "id": 1,
  "fileName": "7481774e.png",
  "url": "/uploads/7481774e.png",
  "mimeType": "image/png",
  "width": 1584,
  "height": 396
}

🔹 Transform Image

POST /images/{id}/transform

Headers:

Authorization: Bearer <token>
Content-Type: application/json
Accept: application/json


Body:

{
  "transformations": {
    "resize": { "width": 400, "height": 300 },
    "rotate": 90,
    "filters": { "grayscale": true },
    "format": "png"
  }
}


Response:

{
  "id": 2,
  "fileName": "transformed.png",
  "url": "/uploads/transformed.png",
  "mimeType": "image/png",
  "width": 400,
  "height": 300
}

🔹 Retrieve Image

GET /images/{id}
Returns raw image bytes.
Set Accept: image/png or open directly in browser.

🔹 List Images

GET /images?page=0&limit=10

Response:

{
  "content": [
    {
      "id": 1,
      "fileName": "banner.png",
      "url": "/uploads/banner.png"
    }
  ],
  "totalElements": 1
}