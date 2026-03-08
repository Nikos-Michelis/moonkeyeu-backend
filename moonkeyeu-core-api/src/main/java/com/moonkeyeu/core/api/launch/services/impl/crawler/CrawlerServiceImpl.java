package com.moonkeyeu.core.api.launch.services.impl.crawler;

import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;
import com.moonkeyeu.core.api.launch.model.SocialMediaCrawler;
import com.moonkeyeu.core.api.launch.services.CrawlerService;
import com.moonkeyeu.core.api.settings.exceptions.InvalidUserAgentException;
import com.moonkeyeu.core.api.utils.meta.MetaElementUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    @Value("${application.frontend.url}")
    private String frontendUrl;
    private final MetaElementUtil metaElementUtil;


    @Override
    public ResponseEntity<Object> getMetaByTypeAndId(String userAgent, Object id, String type, CrawlerDTO crawlerDTO) {

        String clientAppUrl =
                UriComponentsBuilder
                        .fromUri(URI.create(frontendUrl))
                        .pathSegment(type.toLowerCase())
                        .pathSegment(id.toString()).toUriString();

        if (isCrawler(userAgent)) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", clientAppUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        String jsonLdScript = metaElementUtil.buildJsonLdScript(crawlerDTO, clientAppUrl);
        String metaTags = metaElementUtil.buildMetaOg(crawlerDTO, "article", clientAppUrl, jsonLdScript);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(metaTags);
    }

    @Override
    public ResponseEntity<Object> getMetaByType(String userAgent, String type, CrawlerDTO crawlerDTO) {

        String clientAppUrl =
                UriComponentsBuilder
                        .fromUri(URI.create(frontendUrl))
                        .pathSegment(type.toLowerCase())
                        .toUriString();

        if (isCrawler(userAgent)) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", clientAppUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        String jsonLdScript = metaElementUtil.buildJsonLdScript(crawlerDTO, clientAppUrl);
        String metaTags = metaElementUtil.buildMetaOg(crawlerDTO, "website", clientAppUrl, jsonLdScript);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(metaTags);
    }


    private boolean isCrawler(String userAgent) {
        if (userAgent == null) throw new InvalidUserAgentException("userAgent should not be null, please provide the userAgent.");
        String agent = userAgent.toLowerCase();
        return Arrays.stream(SocialMediaCrawler.values())
                .noneMatch(crawler -> crawler.getIdentifier().contains(agent));
    }

}
