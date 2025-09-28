package com.example.images.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @Value("${app.jwt.header:Authorization}")
    private String header;

    @Value("${app.jwt.prefix:Bearer}")
    private String prefix;
}
