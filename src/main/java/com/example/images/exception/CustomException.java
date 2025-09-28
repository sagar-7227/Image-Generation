package com.example.images.exception;

import lombok.Data;

@Data
public class CustomException extends RuntimeException {

    private int statusCode = 400; // Default: Bad Request

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}

