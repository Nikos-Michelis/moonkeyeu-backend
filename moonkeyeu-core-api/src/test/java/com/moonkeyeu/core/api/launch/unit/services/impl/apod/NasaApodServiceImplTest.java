package com.moonkeyeu.core.api.launch.unit.services.impl.apod;

import com.moonkeyeu.core.api.launch.services.impl.apod.NasaApodServiceImpl;
import com.moonkeyeu.core.api.utils.caching.CacheManagerUtil;
import com.moonkeyeu.core.api.utils.caching.CacheNames;
import com.moonkeyeu.core.api.launch.dto.NasaApodDTO;
import com.moonkeyeu.core.api.launch.services.NasaApodClientService;
import com.moonkeyeu.core.api.settings.exceptions.NasaApodFetchException;
import com.moonkeyeu.core.api.settings.exceptions.RemoteServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NasaApodServiceImplTest Unit Tests")
class NasaApodServiceImplTest {

    @Mock
    private NasaApodClientService nasaApodClientService;
    @Mock
    private CacheManagerUtil cacheManagerUtil;
    @Mock
    private Cache cache;
    private NasaApodDTO testNasaApodCached;
    private NasaApodDTO testNasaApodLatest;
    @InjectMocks
    private NasaApodServiceImpl apodService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(apodService, "mediaType", "image");
        this.testNasaApodCached = NasaApodDTO.builder().media_type("image").date(LocalDate.now().minusDays(1)).build();
        this.testNasaApodLatest = NasaApodDTO.builder().media_type("image").date(LocalDate.now()).build();
    }

    @Test
    @DisplayName("Should put in cache latest nasa apod image when is empty")
    void shouldPutInCacheWhenEmpty() {
        // Given
        when(cacheManagerUtil.getCacheByName(CacheNames.NASA_APOD_CACHE))
                .thenReturn(cache);
        when(cache.get("today", NasaApodDTO.class))
                .thenReturn(null);
        when(nasaApodClientService.fetchNasaAstronomyPictureOfTheDay())
                .thenReturn(this.testNasaApodCached);


        // when & then
        apodService.refreshNasaApod();

        verify(cache).put("today", this.testNasaApodCached);
    }

    @Test
    @DisplayName("Should update cache when fetch latest nasa apod image")
    void shouldUpdateCacheWhenOlder() {
        // Given
        when(cacheManagerUtil.getCacheByName(CacheNames.NASA_APOD_CACHE))
                .thenReturn(cache);
        when(cache.get("today", NasaApodDTO.class))
                .thenReturn(testNasaApodCached);

        when(nasaApodClientService.fetchNasaAstronomyPictureOfTheDay())
                .thenReturn(testNasaApodLatest);

        // when & then
        apodService.refreshNasaApod();

        verify(cache).put("today", testNasaApodLatest);
    }

    @Test
    @DisplayName("Should not update cache when newer data already exists")
    void shouldNotUpdateWhenCachedIsNewer() {
        // Given
        when(cacheManagerUtil.getCacheByName(CacheNames.NASA_APOD_CACHE))
                .thenReturn(cache);
        when(cache.get("today", NasaApodDTO.class))
                .thenReturn(testNasaApodCached);

        when(nasaApodClientService.fetchNasaAstronomyPictureOfTheDay())
                .thenReturn(testNasaApodCached);

        // when & then
        apodService.refreshNasaApod();

        verify(cache, never()).put(any(), any());
    }

    @Test
    @DisplayName("Should not update cache when the fetched media type is not 'image'")
    void shouldNotCacheWhenMediaTypeInvalid() {
        // Given
        NasaApodDTO latest = NasaApodDTO.builder().media_type("video").date(LocalDate.now()).build();
        when(cacheManagerUtil.getCacheByName(CacheNames.NASA_APOD_CACHE))
                .thenReturn(cache);
        when(cache.get("today", NasaApodDTO.class)).thenReturn(null);
        when(nasaApodClientService.fetchNasaAstronomyPictureOfTheDay())
                .thenReturn(latest);
        // When & Then
        apodService.refreshNasaApod();

        verify(cache, never()).put(any(), any());
    }

    @Test
    @DisplayName("Should keep old cache when latest fetch fails")
    void shouldKeepCacheWhenFetchFails() {
        // Given
        when(cacheManagerUtil.getCacheByName(CacheNames.NASA_APOD_CACHE))
                .thenReturn(cache);
        when(cache.get("today", NasaApodDTO.class)).thenReturn(this.testNasaApodCached);
        when(nasaApodClientService.fetchNasaAstronomyPictureOfTheDay())
                .thenThrow(new NasaApodFetchException("fail"));
        // When & Then
        apodService.refreshNasaApod();

        verify(cache, never()).put(any(), any());
    }

    @Test
    @DisplayName("Should return cached nasa apod image")
    void shouldReturnValueFromCache() {
        // Given
        when(cacheManagerUtil.getByCacheNameAndKey(CacheNames.NASA_APOD_CACHE, "today", NasaApodDTO.class))
                .thenReturn(Optional.of(testNasaApodCached));
        // When & Then
        NasaApodDTO result = apodService.getNasaApodFromCache();
        assertThat(result).isEqualTo(testNasaApodCached);
    }

    @Test
    @DisplayName("Should throw RemoteServiceUnavailableException when cache is empty")
    void shouldThrowWhenCacheEmpty() {
        // Given
        when(cacheManagerUtil.getByCacheNameAndKey(CacheNames.NASA_APOD_CACHE, "today", NasaApodDTO.class))
                .thenReturn(Optional.empty());
        // When & Then
        assertThatThrownBy(() ->
                apodService.getNasaApodFromCache()
        ).isInstanceOf(RemoteServiceUnavailableException.class);
    }
}