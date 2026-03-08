package com.moonkeyeu.core.api.launch.unit.services.impl.crawler;

import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;
import com.moonkeyeu.core.api.launch.services.impl.crawler.CrawlerServiceImpl;
import com.moonkeyeu.core.api.utils.meta.MetaElementUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrawlerServiceImpl Unit Tests")
class CrawlerServiceTest {
    @Mock
    private MetaElementUtil metaElementUtil;
    @InjectMocks
    private CrawlerServiceImpl crawlerService;
    private CrawlerDTO articleCrawlerDTO;
    private CrawlerDTO pageCrawlerDTO;

    @BeforeEach
    void setUp() {
        this.pageCrawlerDTO = CrawlerDTO.builder()
                .description("Stay up to date with upcoming and past spaceflight from NASA, SpaceX, and other leading space agencies around the world.")
                .image("https://cdn.example.com/media/assets/logo/moonkeyeu-logo.png")
                .dateModified(Instant.now())
                .datePublished(Instant.now())
                .build();
        this.articleCrawlerDTO = CrawlerDTO.builder()
                .title("Falcon 9 launches Starlink 8-1")
                .description("SpaceX launches a Falcon 9 rocket carrying the next batch of Starlink satellites to low Earth orbit.")
                .image("https://cdn.example.com/media/assets/logo/moonkeyeu-logo.png")
                .dateModified(Instant.now())
                .datePublished(Instant.now())
                .build();
        String url = "https://www.example.com";
        ReflectionTestUtils.setField(crawlerService, "frontendUrl", url);
    }

    @Test
    void shouldReturnMetaByTypeAndId() {
        // given
        String userAgent = "facebookexternalhit";
        String type = "launches";
        String id = "123";

        when(metaElementUtil.buildJsonLdScript(any(), any()))
                .thenReturn("jsonLd");

        when(metaElementUtil.buildMetaOg(any(), any(), any(), any()))
                .thenReturn("metaOg");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        // when
        ResponseEntity<Object> response =
                crawlerService.getMetaByTypeAndId(userAgent, id, type, articleCrawlerDTO);
        // then
        assertNotNull(response);
        assertEquals(MediaType.TEXT_HTML, response.getHeaders().getContentType());
        assertEquals("metaOg", response.getBody());

        verify(metaElementUtil)
                .buildJsonLdScript(eq(articleCrawlerDTO), urlCaptor.capture());

        assertEquals("https://www.example.com/" + type + "/123", urlCaptor.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"launches", "missions", "astronauts"})
    void shouldReturnMetaByType(String type) {
        // given
        String userAgent = "facebookexternalhit";

        when(metaElementUtil.buildJsonLdScript(any(), any()))
                .thenReturn("testJsonLdScript");

        when(metaElementUtil.buildMetaOg(any(), any(), any(), any()))
                .thenReturn("testMetaOg");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        // when
        ResponseEntity<Object> response =
                crawlerService.getMetaByType(userAgent, type, pageCrawlerDTO);
        // then
        assertNotNull(response);
        assertEquals(MediaType.TEXT_HTML, response.getHeaders().getContentType());
        assertEquals("testMetaOg", response.getBody());

        verify(metaElementUtil).buildJsonLdScript(eq(pageCrawlerDTO), urlCaptor.capture());

        assertEquals("https://www.example.com/" + type, urlCaptor.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"launches", "agency", "astronauts", "spacecraft"})
    void shouldRedirectToFrontendWhenGetMetaByType(String type) {
        // given
        String userAgent = "Mozilla/5.0";
        // when
        ResponseEntity<Object> response =
                crawlerService.getMetaByType(userAgent, type, pageCrawlerDTO);
        // then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is3xxRedirection());
        verifyNoInteractions(metaElementUtil);
    }

    @ParameterizedTest
    @ValueSource(strings = {"launches", "agency", "astronauts", "spacecraft"})
    void shouldRedirectToFrontendWhenGetMetaByTypeAndId(String type) {
        // given
        String userAgent = "Mozilla/5.0";
        String id = "123";
        // when
        ResponseEntity<Object> response =
                crawlerService.getMetaByTypeAndId(userAgent, type, id, pageCrawlerDTO);
        // then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is3xxRedirection());
        verifyNoInteractions(metaElementUtil);
    }

}