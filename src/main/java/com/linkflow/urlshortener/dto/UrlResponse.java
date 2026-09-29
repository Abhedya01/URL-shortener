package com.linkflow.urlshortener.dto;

import com.linkflow.urlshortener.entity.Url;

import java.time.Instant;

public class UrlResponse {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private Instant createdAt;
    private Instant expiresAt;

    public UrlResponse(
            Long id,
            String originalUrl,
            String shortCode,
            String shortUrl,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static UrlResponse from(Url url) {

        return new UrlResponse(
                url.getId(),
                url.getOriginalUrl(),
                url.getShortCode(),
                "http://localhost:8081/" + url.getShortCode(),
                url.getCreatedAt(),
                url.getExpiresAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}