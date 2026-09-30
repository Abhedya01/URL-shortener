package com.linkflow.urlshortener.service;

import com.linkflow.urlshortener.dto.*;
import com.linkflow.urlshortener.entity.Url;
import com.linkflow.urlshortener.exception.ShortUrlNotFoundException;
import com.linkflow.urlshortener.repository.UrlRepository;
import com.linkflow.urlshortener.util.Base62Util;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;

import java.security.SecureRandom;
import java.time.Instant;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final CacheManager cacheManager;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int SHORT_CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public UrlService(UrlRepository urlRepository, CacheManager cacheManager) {
        this.urlRepository = urlRepository;
        this.cacheManager = cacheManager;
    }

    // CREATE
    @Transactional
    public UrlResponse createShortUrl(CreateUrlRequest request) {

        Url url = new Url(request.getOriginalUrl(), null);
        url.setExpiresAt(request.getExpiresAt());
        Url savedUrl = urlRepository.save(url);
        String shortCode = Base62Util.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);
        savedUrl = urlRepository.save(savedUrl);
        return UrlResponse.from(savedUrl);
    }

    // GET ALL
    public Page<UrlResponse> getAllUrls(Pageable pageable) {

        return urlRepository.findAll(pageable)
                .map(UrlResponse::from);
    }

    public Page<UrlResponse> searchUrls(
            String query,
            Pageable pageable
    ) {

        return urlRepository
                .findByOriginalUrlContainingIgnoreCase(query, pageable)
                .map(UrlResponse::from);
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

        String shortCode = url.getShortCode();

        url.setOriginalUrl(request.getOriginalUrl());
        url.setExpiresAt(request.getExpiresAt());

        Url updatedUrl = urlRepository.save(url);

        urlRepository.flush();

        // Remove old cached value
        org.springframework.cache.Cache cache =
                cacheManager.getCache("urls");

        if (cache != null) {
            cache.evict(shortCode);
        }

        return UrlResponse.from(updatedUrl);
    }


    // DELETE
    public void deleteUrl(Long id) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(
                                "URL not found with id: " + id
                        ));

        String shortCode = url.getShortCode();

        urlRepository.delete(url);

        org.springframework.cache.Cache cache =
                cacheManager.getCache("urls");

        if (cache != null) {
            cache.evict(shortCode);
        }
    }

    // REDIRECT
    @Cacheable(value = "urls", key = "#shortCode")
    public String getOriginalUrl(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found: " + shortCode));

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(Instant.now())) {
            throw new ShortUrlNotFoundException("Short URL has expired: " + shortCode);
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