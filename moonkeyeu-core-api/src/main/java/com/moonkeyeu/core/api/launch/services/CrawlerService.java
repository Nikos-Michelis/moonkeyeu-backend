package com.moonkeyeu.core.api.launch.services;

import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;
import org.springframework.http.ResponseEntity;

public interface CrawlerService {
    ResponseEntity<Object> getMetaByTypeAndId(String userAgent, Object id, String type, CrawlerDTO crawlerDTO);
    ResponseEntity<Object> getMetaByType(String userAgent, String type, CrawlerDTO crawlerDTO);
}
