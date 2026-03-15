package com.moonkeyeu.core.api.launch.controller;
import com.moonkeyeu.core.api.launch.services.*;
import com.moonkeyeu.core.api.security.limiter.RateLimited;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;


@RestController
@RequestMapping("crawler")
@RequiredArgsConstructor
public class CrawlerController {
    private final CrawlerService crawlerService;
    @Value("${application.frontend.url}")
    private String frontendUrl;

    @GetMapping("/default")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<Object> getDefaultPreview(
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent
    ) {

        if (crawlerService.isCrawler(userAgent)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl))
                    .build();
        }

        String seo = crawlerService.getDefaultMetaHtml(frontendUrl);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(seo);
    }

    @GetMapping("/{segment}")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<Object> getDefaultPreviewBySegment(
            @PathVariable String segment,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        String url = UriComponentsBuilder
                .fromUri(URI.create(frontendUrl))
                .pathSegment(segment.toLowerCase())
                .toUriString();

        if (crawlerService.isCrawler(userAgent)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }

        String seo = crawlerService.getMetaHtmlBySegment(url, segment);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(seo);
    }

    @GetMapping("/launch/{id}")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<Object> getLaunchPreview(
            @PathVariable String id,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        String url = UriComponentsBuilder
                .fromUri(URI.create(frontendUrl))
                .pathSegment("launches")
                .pathSegment(id).toUriString();

        if (crawlerService.isCrawler(userAgent)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }

        String seo = crawlerService.getLaunchMetaHtml(id, url);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(seo);
    }
   @GetMapping("/astronaut/{id}")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<Object> getAstronautPreview(
            @PathVariable Integer id,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
       String url = UriComponentsBuilder
               .fromUri(URI.create(frontendUrl))
               .pathSegment("astronauts")
               .pathSegment(id.toString()).toUriString();

       if (crawlerService.isCrawler(userAgent)) {
           return ResponseEntity.status(HttpStatus.FOUND)
                   .location(URI.create(url))
                   .build();
       }

       String seo = crawlerService.getAstronautMetaHtml(id, url);

       return ResponseEntity.ok()
               .contentType(MediaType.TEXT_HTML)
               .body(seo);
    }
    @GetMapping("/program/{id}")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<Object> getProgramPreview(
            @PathVariable Integer id,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        String url = UriComponentsBuilder
                .fromUri(URI.create(frontendUrl))
                .pathSegment("programs")
                .pathSegment(id.toString()).toUriString();

        if (crawlerService.isCrawler(userAgent)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }

        String seo = crawlerService.getProgramMetaHtml(id, url);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(seo);
    }
    @GetMapping("/spacecraft/{id}")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<Object> getSpacecraftPreview(
            @PathVariable Integer id,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {

        String url = UriComponentsBuilder
                .fromUri(URI.create(frontendUrl))
                .pathSegment("vehicles")
                .pathSegment("spacecraft")
                .path(id.toString()).toUriString();

        if (crawlerService.isCrawler(userAgent)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }

        String seo = crawlerService.getSpacecraftMetaHtml(id, url);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(seo);

    }
    @GetMapping("/launch-pad/{id}")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<Object> getLaunchPadPreview(
            @PathVariable Integer id,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        String url = UriComponentsBuilder
                .fromUri(URI.create(frontendUrl))
                .pathSegment("locations")
                .path(id.toString()).toUriString();

        if (crawlerService.isCrawler(userAgent)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }

        String seo = crawlerService.getLaunchPadMetaHtml(id, url);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(seo);
    }
    @GetMapping("/agency/{id}")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<Object> getAgencyPreview(
            @PathVariable Integer id,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {

        String url = UriComponentsBuilder
                .fromUri(URI.create(frontendUrl))
                .pathSegment("agencies")
                .path(id.toString()).toUriString();

        if (crawlerService.isCrawler(userAgent)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }

        String seo = crawlerService.getAgencyMetaHtml(id, url);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(seo);
    }
}
