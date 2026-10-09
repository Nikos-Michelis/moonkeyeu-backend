package com.moonkeyeu.core.api.utils.caching;


import com.moonkeyeu.core.api.settings.exceptions.CacheInitializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Component
@Slf4j
public class CacheManagerUtil {

    private final CacheManager cacheManager;

    public CacheManagerUtil(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    public void evictAll(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("Evicted all entries from cache '{}'", cacheName);
        } else {
            log.warn("Cache '{}' not found", cacheName);
        }
    }
    public void evictById(String cacheName, String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evictIfPresent(cacheKey);
            log.info("Evicted cache entry with key '{}' from cache '{}'", cacheKey, cacheName);
        } else {
            log.warn("Cache '{}' not found", cacheName);
        }
    }
    public <T> Optional<T> getByCacheNameAndKey(String cacheName, String cacheKey, Class<T> type){
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            throw new CacheInitializationException("Cache " + cacheName + " not configured");
        }

        return Optional.ofNullable(cache.get(cacheKey, type));
    }
    public Cache getCacheByName(String cacheName){
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            throw new CacheInitializationException("Cache " + cacheName + " not configured");
        }

        return cache;
    }
}
