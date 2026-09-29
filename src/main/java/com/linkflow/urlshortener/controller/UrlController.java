package com.linkflow.urlshortener.controller;

import com.linkflow.urlshortener.dto.*;
import com.linkflow.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // CREATE
    @PostMapping("/api/urls")
    public ResponseEntity<UrlResponse> createUrl(@Valid @RequestBody CreateUrlRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.createShortUrl(request));
    }

    // GET ALL
    @GetMapping("/api/urls")
    public ResponseEntity<List<UrlResponse>> getAllUrls() {

        return ResponseEntity.ok(
                urlService.getAllUrls()
        );
    }

    // GET BY ID
    @GetMapping("/api/urls/{id}")
    public ResponseEntity<UrlResponse> getUrlById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                urlService.getUrlById(id)
        );
    }

    // UPDATE
    @PutMapping("/api/urls/{id}")
    public ResponseEntity<UrlResponse> updateUrl(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUrlRequest request
    ) {

        return ResponseEntity.ok(
                urlService.updateUrl(id, request)
        );
    }

    // DELETE
    @DeleteMapping("/api/urls/{id}")
    public ResponseEntity<Void> deleteUrl(
            @PathVariable Long id
    ) {

        urlService.deleteUrl(id);

        return ResponseEntity.noContent().build();
    }

    // REDIRECT
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(
            @PathVariable String shortCode
    ) {

        String originalUrl =
                urlService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(
                        HttpHeaders.LOCATION,
                        originalUrl
                )
                .build();
    }
}