package com.linkflow.urlshortener.service;

import com.linkflow.urlshortener.dto.*;
import com.linkflow.urlshortener.entity.Url;
import com.linkflow.urlshortener.exception.ShortUrlNotFoundException;
import com.linkflow.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int SHORT_CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // CREATE
    public UrlResponse createShortUrl(CreateUrlRequest request) {

        String shortCode = generateUniqueShortCode();

        Url url = new Url(
                request.getOriginalUrl(),
                shortCode
        );

        url.setExpiresAt(request.getExpiresAt());

        Url savedUrl = urlRepository.save(url);

        return UrlResponse.from(savedUrl);
    }

    // GET ALL
    public List<UrlResponse> getAllUrls() {

        return urlRepository.findAll()
                .stream()
                .map(UrlResponse::from)
                .toList();
    }

    // GET BY ID
    public UrlResponse getUrlById(Long id) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(
                                "URL not found with id: " + id
                        ));

        return UrlResponse.from(url);
    }

    // UPDATE
    public UrlResponse updateUrl(
            Long id,
            UpdateUrlRequest request
    ) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(
                                "URL not found with id: " + id
                        ));

        url.setOriginalUrl(request.getOriginalUrl());
        url.setExpiresAt(request.getExpiresAt());

        Url updatedUrl = urlRepository.save(url);

        return UrlResponse.from(updatedUrl);
    }

    // DELETE
    public void deleteUrl(Long id) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(
                                "URL not found with id: " + id
                        ));

        urlRepository.delete(url);
    }

    // REDIRECT
    public String getOriginalUrl(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(
                                "Short URL not found: " + shortCode
                        ));

        if (url.getExpiresAt() != null &&
                url.getExpiresAt().isBefore(Instant.now())) {

            throw new ShortUrlNotFoundException(
                    "Short URL has expired: " + shortCode
            );
        }

        return url.getOriginalUrl();
    }

    private String generateUniqueShortCode() {

        String shortCode;

        do {
            shortCode = generateShortCode();
        } while (urlRepository.existsByShortCode(shortCode));

        return shortCode;
    }

    private String generateShortCode() {

        StringBuilder shortCode = new StringBuilder();

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {

            int index =
                    random.nextInt(CHARACTERS.length());

            shortCode.append(
                    CHARACTERS.charAt(index)
            );
        }

        return shortCode.toString();
    }
}