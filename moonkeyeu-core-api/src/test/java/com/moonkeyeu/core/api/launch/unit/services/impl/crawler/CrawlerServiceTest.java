package com.moonkeyeu.core.api.launch.unit.services.impl.crawler;

import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;
import com.moonkeyeu.core.api.launch.dto.ImageDTO;
import com.moonkeyeu.core.api.launch.dto.agency.AgencyDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.astronaut.AstronautDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchDTO;
import com.moonkeyeu.core.api.launch.dto.pad.LaunchPadDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.program.ProgramDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketConfigurationDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.spacecraft.SpacecraftConfigurationDTO;
import com.moonkeyeu.core.api.launch.model.MetaType;
import com.moonkeyeu.core.api.launch.services.*;
import com.moonkeyeu.core.api.launch.services.impl.crawler.CrawlerServiceImpl;
import com.moonkeyeu.core.api.settings.exceptions.InvalidUserAgentException;
import com.moonkeyeu.core.api.utils.meta.MetaElementUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrawlerServiceImpl Unit Tests")
class CrawlerServiceTest {
    @Mock
    private MetaElementUtil metaElementUtil;
    @Mock
    private LaunchService launchService;
    @Mock
    private AstronautService astronautService;
    @Mock
    private ProgramsService programsService;
    @Mock
    private SpacecraftService spacecraftService;
    @Mock
    private LaunchPadService launchPadService;
    @Mock
    private AgenciesService agenciesService;
    @Spy
    @InjectMocks
    private CrawlerServiceImpl crawlerService;
    private ProgramDetailedDTO programDetailedDTO;
    private CrawlerDTO pageCrawlerDTO;
    private LaunchDTO launchDTO;
    private AstronautDetailedDTO astronautDTO;
    private SpacecraftConfigurationDTO spacecraftConfiguration;
    private LaunchPadDetailedDTO launchPadDetailedDTO;
    private AgencyDetailedDTO agencyDetailedDTO;
    private final String DEFAULT_IMG_URL = "https://cdn.moonkeyeu.com/media/assets/logo/moonkeyeu-logo.jpg";

    @BeforeEach
    void setUp() {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageId(1L);
        imageDTO.setImageUrl("https://cdn.example.com/assets/test.png");

        RocketConfigurationDTO testRocketConfiguration = new RocketConfigurationDTO();
        testRocketConfiguration.setRocketConfId(2L);
        testRocketConfiguration.setRocketName("Falcon 9 Block 5");
        testRocketConfiguration.setDescription("test_description");
        testRocketConfiguration.setActive(true);

        RocketDetailedDTO testRocket = new RocketDetailedDTO();
        testRocket.setRocketId(1);
        testRocket.setRocketConfiguration(testRocketConfiguration);

        this.launchDTO = new LaunchDTO();
        this.launchDTO.setLaunchId("0041d2ac-62cb-416f-b67a-cd0277f1cfe2");
        this.launchDTO.setLaunchName("Falcon 9 Block 5 | Crew-12");
        this.launchDTO.setRocket(testRocket);
        this.launchDTO.setRocketConfImages(imageDTO);

        this.astronautDTO = new AstronautDetailedDTO();
        this.astronautDTO.setAstronautId(274L);
        this.astronautDTO.setName("Yury Usachov");
        this.astronautDTO.setInSpace(false);
        this.astronautDTO.setAstronautImages(Set.of(imageDTO));

        this.programDetailedDTO = new ProgramDetailedDTO();
        this.programDetailedDTO.setProgramId(2L);
        this.programDetailedDTO.setName("Starlink");
        this.programDetailedDTO.setProgramImages(Set.of(imageDTO));

        this.spacecraftConfiguration = new SpacecraftConfigurationDTO();
        this.spacecraftConfiguration.setSpacecraftConfId(2L);
        this.spacecraftConfiguration.setSpacecraftConfName("Crew Dragon 2");
        this.spacecraftConfiguration.setSpacecraftConfImages(Set.of(imageDTO));

        this.launchPadDetailedDTO = new LaunchPadDetailedDTO();
        this.launchPadDetailedDTO.setLaunchPadId(2L);
        this.launchPadDetailedDTO.setLaunchPadName("Cape Canaveral");
        this.launchPadDetailedDTO.setMapImage(imageDTO.getImageUrl());

        this.agencyDetailedDTO = new AgencyDetailedDTO();
        this.agencyDetailedDTO.setAgencyId(2L);
        this.agencyDetailedDTO.setAgencyName("Space X");
        this.agencyDetailedDTO.setAgenciesImages(Set.of(imageDTO));

        this.pageCrawlerDTO = CrawlerDTO.builder()
                .description("Stay up to date with upcoming and past spaceflight from NASA, SpaceX, and other leading space agencies around the world.")
                .image("https://cdn.example.com/media/assets/logo/moonkeyeu-logo.png")
                .dateModified(Instant.now())
                .datePublished(Instant.now())
                .build();
     }

    @Test
    void shouldReturnMetaHtml() {
        // given
        String url = "https://www.example.com";
        String expected = "<meta>";

        when(metaElementUtil.buildJsonLdScript(any(), any()))
                .thenReturn("<json>");

        when(metaElementUtil.buildMetaOg(any(), any(), any(), any()))
                .thenReturn(expected);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        // when
        String result = crawlerService.getMetaHtml(MetaType.WEBSITE.getIdentifier(), url, pageCrawlerDTO);
        // then
        assertNotNull(result);
        assertEquals(expected, result);

        verify(metaElementUtil).buildJsonLdScript(eq(pageCrawlerDTO), urlCaptor.capture());

        assertEquals("https://www.example.com", urlCaptor.getValue());
    }

    @Test
    void shouldReturnDefaultMetaHtml() {
        String url = "https://www.example.com";
        String expected = "<meta>";

        when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
        when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

        String result = crawlerService.getDefaultMetaHtml(url);

        assertNotNull(result);
        assertEquals(expected, result);

        verify(crawlerService, times(1)).getMetaHtml(eq(MetaType.WEBSITE.getIdentifier()), eq(url), any(CrawlerDTO.class));
        verify(crawlerService, times(1)).getDefaultMetaHtml(eq(url));
    }

    @ParameterizedTest
    @ValueSource(strings = {"launches", "agency", "astronauts", "spacecraft"})
    void shouldReturnMetaHtmlBySegment(String segment) {
        String expected = "<meta>";
        String url = UriComponentsBuilder
                .fromUri(URI.create("https://www.example.com"))
                .pathSegment(segment.toLowerCase())
                .toUriString();

        when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
        when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

        String result = crawlerService.getMetaHtmlBySegment(url, segment);

        assertEquals(expected, result);
        verify(crawlerService).getMetaHtml(eq(MetaType.WEBSITE.getIdentifier()), eq(url), any(CrawlerDTO.class));
    }

    @Nested
    @DisplayName("Should validate and detect User-Agent types")
    class CheckUserAgent {

        @Test
        void shouldReturnTrue_WhenUserAgentIsBot() {
            String userAgent = "facebookexternalhit";

            when(crawlerService.isCrawler(userAgent)).thenReturn(true);

            boolean result = crawlerService.isCrawler(userAgent);

            assertTrue(result);

            verify(crawlerService)
                    .isCrawler(argThat(userAgent::equals));
        }

        @Test
        void shouldReturnFalse_WhenUserAgentIsNotBot() {
            String userAgent = "Chrome v.1.0";

            when(crawlerService.isCrawler(userAgent)).thenReturn(false);

            boolean result = crawlerService.isCrawler(userAgent);

            assertFalse(result);

            verify(crawlerService)
                    .isCrawler(argThat(userAgent::equals));
        }

        @Test
        void shouldThrowInvalidUserAgentException_WhenUserAgentIsNull() {
            String userAgent = null;
            // when & Then
            final InvalidUserAgentException exception = assertThrows(
                    InvalidUserAgentException.class,
                    ()-> crawlerService.isCrawler(userAgent));

            assertNotNull(exception);
            assertEquals("User-Agent should not be null, please provide the User-Agent.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Return meta html by launch id")
    class LaunchMetaHtml {

        @Test
        void shouldReturnLaunchMetaTagsAndJsonLd() {
            String id = "e1b6d391-fa37-47a5-9a18-7b19a8a183d8";
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("launches")
                    .pathSegment(id)
                    .toUriString();

            String expected = "<meta>";
            when(launchService.getLaunchById(anyString())).thenReturn(launchDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getLaunchMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), any(CrawlerDTO.class));
            verify(crawlerService)
                    .getLaunchMetaHtml(eq(id), eq(url));
        }

        @Test
        void shouldReturnLaunchMetaTagsAndJsonLd_DefaultImageUrl() {
            String id = "e1b6d391-fa37-47a5-9a18-7b19a8a183d8";
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("launches")
                    .pathSegment(id)
                    .toUriString();
            launchDTO.setRocketConfImages(null);
            String expected = "<meta>";

            when(launchService.getLaunchById(anyString())).thenReturn(launchDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getLaunchMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(DEFAULT_IMG_URL)));
            verify(crawlerService)
                    .getLaunchMetaHtml(eq(id), eq(url));
        }
    }

    @Nested
    @DisplayName("Return meta html by astronaut_id")
    class AstronautMetaHtml {
        @Test
        void shouldReturnAstronautMetaHtml() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("astronauts")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            String expectedImageUrl = "https://cdn.example.com/assets/test.png";

            when(astronautService.getAstronautById(id)).thenReturn(astronautDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getAstronautMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(expectedImageUrl)));
            verify(crawlerService)
                    .getAstronautMetaHtml(eq(id), eq(url));
        }

        @Test
        void shouldReturnAstronautMetaHtml_DefaultImageUrl() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("astronauts")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            astronautDTO.setAstronautImages(null);

            when(astronautService.getAstronautById(id)).thenReturn(astronautDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getAstronautMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(DEFAULT_IMG_URL)));
            verify(crawlerService)
                    .getAstronautMetaHtml(eq(id), eq(url));
        }
    }

    @Nested
    @DisplayName("Return meta html by program_id")
    class ProgramMetaHtml {

        @Test
        void shouldReturnProgramMetaHtml() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("programs")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            String expectedImageUrl = "https://cdn.example.com/assets/test.png";

            when(programsService.getProgramById(id)).thenReturn(programDetailedDTO);

            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getProgramMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(expectedImageUrl)));
            verify(crawlerService)
                    .getProgramMetaHtml(eq(id), eq(url));
        }

        @Test
        void shouldReturnProgramMetaHtml_DefaultImageUrl() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("programs")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            programDetailedDTO.setProgramImages(null);

            when(programsService.getProgramById(id)).thenReturn(programDetailedDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getProgramMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(DEFAULT_IMG_URL)));
            verify(crawlerService)
                    .getProgramMetaHtml(eq(id), eq(url));
        }
    }

    @Nested
    @DisplayName("Return meta html by spacecraft_id")
    class SpacecraftMetaHtml {
        @Test
        void shouldReturnSpacecraftMetaHtml() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("vehicles/spacecraft")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            String expectedImageUrl = "https://cdn.example.com/assets/test.png";

            when(spacecraftService.getSpacecraftById(id)).thenReturn(spacecraftConfiguration);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getSpacecraftMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(expectedImageUrl)));
            verify(crawlerService)
                    .getSpacecraftMetaHtml(eq(id), eq(url));
        }

         @Test
        void shouldReturnSpacecraftMetaHtml_DefaultImageUrl() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("vehicles/spacecraft")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            spacecraftConfiguration.setSpacecraftConfImages(null);

            when(spacecraftService.getSpacecraftById(id)).thenReturn(spacecraftConfiguration);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getSpacecraftMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(DEFAULT_IMG_URL)));
            verify(crawlerService)
                    .getSpacecraftMetaHtml(eq(id), eq(url));
        }
    }

    @Nested
    @DisplayName("Return meta html by launchPad_id")
    class LaunchPadMetaHtml {
        @Test
        void shouldReturnLaunchPadMetaHtml() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("locations")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            String expectedImageUrl = "https://cdn.example.com/assets/test.png";

            when(launchPadService.getLaunchPadById(id)).thenReturn(launchPadDetailedDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getLaunchPadMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(expectedImageUrl)));
            verify(crawlerService)
                    .getLaunchPadMetaHtml(eq(id), eq(url));
        }

        @Test
        void shouldReturnLaunchPadMetaHtml_DefaultImageUrl() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("locations")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            launchPadDetailedDTO.setMapImage(null);

            when(launchPadService.getLaunchPadById(id)).thenReturn(launchPadDetailedDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getLaunchPadMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(DEFAULT_IMG_URL)));
            verify(crawlerService)
                    .getLaunchPadMetaHtml(eq(id), eq(url));
        }
    }

    @Nested
    @DisplayName("Return meta html by agency_id")
    class AgencyMetaHtml {
        @Test
        void shouldReturnAgencyMetaHtml() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("agencies")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            String expectedImageUrl = "https://cdn.example.com/assets/test.png";

            when(agenciesService.getAgencyById(id)).thenReturn(agencyDetailedDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getAgencyMetaHtml(id, url);

            assertNotNull(result);
            assertEquals(expected, result);

            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(expectedImageUrl)));
            verify(crawlerService)
                    .getAgencyMetaHtml(eq(id), eq(url));
        }

        @Test
        void shouldReturnAgencyMetaHtml_DefaultImageUrl() {
            Integer id = 121;
            String url = UriComponentsBuilder
                    .fromUri(URI.create("https://www.example.com"))
                    .pathSegment("agencies")
                    .pathSegment(id.toString())
                    .toUriString();

            String expected = "<meta>";
            agencyDetailedDTO.setAgenciesImages(null);

            when(agenciesService.getAgencyById(id)).thenReturn(agencyDetailedDTO);
            when(metaElementUtil.buildJsonLdScript(any(), any())).thenReturn("<json>");
            when(metaElementUtil.buildMetaOg(any(), any(), any(), any())).thenReturn(expected);

            String result = crawlerService.getAgencyMetaHtml(id, url);

            assertEquals(expected, result);
            verify(crawlerService)
                    .getMetaHtml(eq(MetaType.ARTICLE.getIdentifier()), eq(url), argThat(crawler -> crawler.getImage().equals(DEFAULT_IMG_URL)));
            verify(crawlerService)
                    .getAgencyMetaHtml(eq(id), eq(url));
        }
    }
}