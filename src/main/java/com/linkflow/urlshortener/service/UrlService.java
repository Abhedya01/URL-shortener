package com.linkflow.urlshortener.service;

import com.linkflow.urlshortener.dto.CreateUrlRequest;
import com.linkflow.urlshortener.dto.CreateUrlResponse;
import com.linkflow.urlshortener.entity.Url;
import com.linkflow.urlshortener.exception.ShortUrlNotFoundException;
import com.linkflow.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public CreateUrlResponse createShortUrl(CreateUrlRequest request) {

        String shortCode = generateUniqueShortCode();
        Url url = new Url(request.getOriginalUrl(), shortCode);
        Url savedUrl = urlRepository.save(url);
        String shortUrl = "http://localhost:8081/" + savedUrl.getShortCode();

        return new CreateUrlResponse(
                savedUrl.getId(),
                savedUrl.getOriginalUrl(),
                savedUrl.getShortCode(),
                shortUrl
        );
    }

    public String getOriginalUrl(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode).orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found: " + shortCode));
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
            int index = random.nextInt(CHARACTERS.length());
            shortCode.append(CHARACTERS.charAt(index));
        }

        return shortCode.toString();
    }
}