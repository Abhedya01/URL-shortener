package com.linkflow.urlshortener.dto;

public class CreateUrlResponse {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;

    public CreateUrlResponse(
            Long id,
            String originalUrl,
            String shortCode,
            String shortUrl
    ) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
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
}