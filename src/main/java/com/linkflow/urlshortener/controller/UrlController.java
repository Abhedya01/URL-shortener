package com.linkflow.urlshortener.controller;

import com.linkflow.urlshortener.dto.CreateUrlRequest;
import com.linkflow.urlshortener.dto.CreateUrlResponse;
import com.linkflow.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<CreateUrlResponse> createUrl(@Valid @RequestBody CreateUrlRequest request) {
        CreateUrlResponse response = urlService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {

        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, originalUrl).build();
    }
}