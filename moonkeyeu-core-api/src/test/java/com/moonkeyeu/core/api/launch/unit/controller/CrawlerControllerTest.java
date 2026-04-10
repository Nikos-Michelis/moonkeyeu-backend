package com.moonkeyeu.core.api.launch.unit.controller;

import com.moonkeyeu.core.api.configuration.security.jwt.JwtAuthenticationFilter;
import com.moonkeyeu.core.api.launch.controller.CrawlerController;
import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchDTO;
import com.moonkeyeu.core.api.launch.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.Instant;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CrawlerController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "application.frontend.url=https://www.example.com",
        "application.seo.logo=https://cdn.example.com/media/logo/logo.jpg",
        "application.seo.description=Stay up to date with upcoming and past spaceflight from NASA, SpaceX, and other leading space agencies around the world."
})
class CrawlerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private LaunchService launchService;
    @MockitoBean
    private AstronautService astronautService;
    @MockitoBean
    private ProgramsService programsService;
    @MockitoBean
    private SpacecraftService spacecraftService;
    @MockitoBean
    private LaunchPadService launchPadService;
    @MockitoBean
    private AgenciesService agenciesService;
    @MockitoBean
    private CrawlerService crawlerService;
    @Value("${application.frontend.url}")
    private String frontendUrl;
    @Value("${application.seo.logo}")
    private String applicationLogo;
    @Value("${application.seo.description}")
    private String applicationDescription;
    private CrawlerDTO defaultCrawler;

    @BeforeEach
    void setUp() {
        LaunchDTO testLaunch = new LaunchDTO();
        testLaunch.setLaunchId("e1b6d391-fa37-47a5-9a18-7b19a8a183d8");
        testLaunch.setLaunchName("Space Shuttle Atlantis / OV-104 | STS-135");

        this.defaultCrawler = CrawlerDTO.builder()
                .description(applicationDescription)
                .image(applicationLogo)
                .datePublished(Instant.now())
                .dateModified(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("SEO preview endpoint by default")
    class CrawlerCommonEndpointTest {

        @Test
        @DisplayName("Should redirect when user agent is bot")
        public void shouldRedirect_whenUserAgentIsBot() throws Exception {
            // given
            String userAgent = "facebookexternalhit";
            when(crawlerService.isCrawler("facebookexternalhit"))
                    .thenReturn(true);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/crawler/default")
                    .header(HttpHeaders.USER_AGENT, userAgent);

            mockMvc.perform(request)
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(frontendUrl))
                    .andExpect(header().string(HttpHeaders.LOCATION, frontendUrl))
                    .andDo(print());

            verify(crawlerService, never()).getDefaultMetaHtml(anyString());
        }

        @Test
        @DisplayName("Should return meta html when user agent is not bot")
        public void shouldReturnMetaHtml_whenUserAgentIsNotBot() throws Exception {
            // given
            String userAgent = "Chrome";
            String mockHtml = "<html><body>SEO Content</body></html>";

            when(crawlerService.isCrawler(userAgent))
                    .thenReturn(false);

            when(crawlerService.getDefaultMetaHtml(frontendUrl))
                    .thenReturn(mockHtml);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/crawler/default")
                    .header(HttpHeaders.USER_AGENT, userAgent);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("SEO preview endpoint by segment")
    class CrawlerSegmentEndpointTest {
        @ParameterizedTest
        @ValueSource(strings = {"launches", "agency", "astronauts", "spacecraft"})
        void shouldReturnMetaHtmlBySegment_whenUserAgentIsNotBot(String segment) throws Exception {
            // given
            String userAgent = "Chrome";
            String url = frontendUrl + "/" + segment ;
            defaultCrawler.setTitle(segment);
            String mockHtml = "<html><body>SEO Content</body></html>";

            when(crawlerService.isCrawler(userAgent))
                    .thenReturn(false);

            when(crawlerService.getMetaHtmlBySegment(url, segment))
                    .thenReturn(mockHtml);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/crawler/{segment}", segment)
                    .header(HttpHeaders.USER_AGENT, userAgent);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML))
                    .andDo(print());
        }

        @ParameterizedTest
        @ValueSource(strings = {"launches", "agency", "astronauts", "spacecraft"})
        public void shouldReturnMetaHtmlBySegment_whenUserAgentIsBot(String segment) throws Exception {
            // given
            String userAgent = "facebookexternalhit";
            String url = frontendUrl + "/" + segment ;

            when(crawlerService.isCrawler(userAgent))
                    .thenReturn(true);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/crawler/{segment}", segment)
                    .header(HttpHeaders.USER_AGENT, userAgent);

            mockMvc.perform(request)
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(url))
                    .andExpect(header().string(HttpHeaders.LOCATION, url))
                    .andDo(print());

            verify(crawlerService, never()).getMetaHtmlBySegment(eq(url), eq(segment));
        }
    }


    @Nested
    @DisplayName("SEO preview endpoints")
    class SeoPreviewEndpointsTest {

        private static Stream<Arguments> previewEndpointProvider() {
            return Stream.of(
                    Arguments.of("/crawler/launch/123", "launches/123", "launch"),
                    Arguments.of("/crawler/astronaut/1", "astronauts/1", "astronaut"),
                    Arguments.of("/crawler/program/5", "programs/5", "program"),
                    Arguments.of("/crawler/spacecraft/10", "vehicles/spacecraft/10", "spacecraft"),
                    Arguments.of("/crawler/launch-pad/20", "locations/20", "launch-pad"),
                    Arguments.of("/crawler/agency/50", "agencies/50", "agency")
            );
        }

        @ParameterizedTest(name = "User Agent: {0}")
        @MethodSource("previewEndpointProvider")
        void shouldReturnMetaHtml_whenUserIsRegularUser(String path, String expectedFrontendPath, String type) throws Exception {
            String userAgent = "Mozilla/5.0 Chrome/120.0";
            String expectedUrl = frontendUrl + "/" + expectedFrontendPath;
            String mockHtml = "<html><body>SEO Content</body></html>";

            when(crawlerService.isCrawler(userAgent)).thenReturn(false);

            switch (type) {
                case "launch" -> when(crawlerService.getLaunchMetaHtml(anyString(), eq(expectedUrl))).thenReturn(mockHtml);
                case "astronaut" -> when(crawlerService.getAstronautMetaHtml(anyInt(), eq(expectedUrl))).thenReturn(mockHtml);
                case "program" -> when(crawlerService.getProgramMetaHtml(anyInt(), eq(expectedUrl))).thenReturn(mockHtml);
                case "spacecraft" -> when(crawlerService.getSpacecraftMetaHtml(anyInt(), eq(expectedUrl))).thenReturn(mockHtml);
                case "launch-pad" -> when(crawlerService.getLaunchPadMetaHtml(anyInt(), eq(expectedUrl))).thenReturn(mockHtml);
                case "agency" -> when(crawlerService.getAgencyMetaHtml(anyInt(), eq(expectedUrl))).thenReturn(mockHtml);
            }

            RequestBuilder request = MockMvcRequestBuilders.get(path)
                    .header(HttpHeaders.USER_AGENT, userAgent);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML))
                    .andExpect(content().string(mockHtml));
        }

        @ParameterizedTest(name = "Bot: {0}")
        @MethodSource("previewEndpointProvider")
        void shouldRedirect_whenUserIsBot(String path, String expectedFrontendPath) throws Exception {
            String userAgent = "googlebot";
            String expectedRedirectUrl = frontendUrl + "/" + expectedFrontendPath;

            when(crawlerService.isCrawler(userAgent)).thenReturn(true);

            RequestBuilder request = MockMvcRequestBuilders.get(path)
                    .header(HttpHeaders.USER_AGENT, userAgent);

            mockMvc.perform(request)
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl(expectedRedirectUrl));
        }
    }
}