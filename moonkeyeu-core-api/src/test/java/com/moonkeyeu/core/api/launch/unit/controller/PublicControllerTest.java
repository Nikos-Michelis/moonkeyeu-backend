package com.moonkeyeu.core.api.launch.unit.controller;

import com.moonkeyeu.core.api.configuration.security.jwt.JwtAuthenticationFilter;
import com.moonkeyeu.core.api.launch.controller.PublicController;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.NasaApodDTO;
import com.moonkeyeu.core.api.launch.dto.agency.AgencyDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.agency.AgencyNormalDTO;
import com.moonkeyeu.core.api.launch.dto.agency.AgencySummarizedDTO;
import com.moonkeyeu.core.api.launch.dto.astronaut.AstronautDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.astronaut.AstronautNormalDTO;
import com.moonkeyeu.core.api.launch.dto.filters.FiltersDTO;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchDTO;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchNormalDTO;
import com.moonkeyeu.core.api.launch.dto.launcher.LauncherDTO;
import com.moonkeyeu.core.api.launch.dto.pad.LaunchPadDTO;
import com.moonkeyeu.core.api.launch.dto.pad.LaunchPadDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.program.ProgramDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.program.ProgramSummarizedDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketConfigSummarizedDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketConfigurationDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketNormalDTO;
import com.moonkeyeu.core.api.launch.dto.spacecraft.SpacecraftConfigSummarizedDTO;
import com.moonkeyeu.core.api.launch.dto.spacecraft.SpacecraftConfigurationDTO;
import com.moonkeyeu.core.api.launch.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PublicController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private SpacecraftService spacecraftService;
    @MockitoBean
    private ProgramsService programsService;
    @MockitoBean
    private AgenciesService agenciesService;
    @MockitoBean
    private LaunchPadService launchPadService;
    @MockitoBean
    private LaunchService launchService;
    @MockitoBean
    private AstronautService astronautService;
    @MockitoBean
    private RocketService rocketService;
    @MockitoBean
    private LauncherService launcherService;
    @MockitoBean
    private FiltersService filtersService;
    @MockitoBean
    private NasaApodService nasaApodService;
    private LaunchDTO testLaunch;
    private AstronautNormalDTO astronautNormalDTO;
    private AstronautDetailedDTO astronautDetailedDTO;
    private RocketConfigSummarizedDTO rocketConfigSummarizedDTO;
    private RocketNormalDTO rocketNormalDTO;
    private SpacecraftConfigSummarizedDTO spacecraftConfigSummarizedDTO;
    private SpacecraftConfigurationDTO  spacecraftConfigurationDTO;
    private ProgramSummarizedDTO programSummarizedDTO;
    private ProgramDetailedDTO programDetailedDTO;
    private AgencySummarizedDTO agencySummarizedDTO;
    private AgencyDetailedDTO agencyDetailedDTO;
    private LaunchPadDTO launchPadDTO;
    private LaunchPadDetailedDTO launchPadDetailedDTO;
    private LauncherDTO launcherDTO;
    private Map<String, Object> launchFilters;
    private Map<String, Object> astronautFilters;
    private NasaApodDTO nasaApodDTO;

    @BeforeEach
    void setUp() {
        AgencyNormalDTO testAgencies = new AgencyNormalDTO();
        testAgencies.setAgencyId(44L);
        testAgencies.setAgencyName("NASA");

        LaunchPadDTO testLaunchPad = new LaunchPadDTO();
        testLaunchPad.setLaunchPadId(87L);
        testLaunchPad.setName("Launch Complex 39A");

        this.agencySummarizedDTO = new AgencySummarizedDTO();
        this.agencySummarizedDTO.setAgencyId(44L);
        this.agencySummarizedDTO.setAgencyName("NASA");
        this.agencySummarizedDTO.setTypeName("Government");

        this.testLaunch = new LaunchDTO();
        this.testLaunch.setLaunchId("e1b6d391-fa37-47a5-9a18-7b19a8a183d8");
        this.testLaunch.setLaunchName("Space Shuttle Atlantis / OV-104 | STS-135");
        this.testLaunch.setAgencies(testAgencies);
        this.testLaunch.setLaunchPad(testLaunchPad);

        LaunchNormalDTO launchNormalDTO = new LaunchNormalDTO();
        launchNormalDTO.setLaunchId("e1b6d391-fa37-47a5-9a18-7b19a8a183d8");
        launchNormalDTO.setLaunchName("Space Shuttle Atlantis / OV-104 | STS-135");
        launchNormalDTO.setAgenciesName("NASA");

        this.astronautNormalDTO = new AstronautNormalDTO();
        this.astronautNormalDTO.setAstronautId(274L);
        this.astronautNormalDTO.setName("Yury Usachov");
        this.astronautNormalDTO.setStatusName("Launch Successful");

        this.astronautDetailedDTO = new AstronautDetailedDTO();
        this.astronautDetailedDTO.setAstronautId(274L);
        this.astronautDetailedDTO.setName("Yury Usachov");

        this.rocketConfigSummarizedDTO = new RocketConfigSummarizedDTO();
        this.rocketConfigSummarizedDTO.setRocketConfId(493L);
        this.rocketConfigSummarizedDTO.setFullname("Space Shuttle");

        RocketConfigurationDTO rocketConfigurationDTO = new RocketConfigurationDTO();
        rocketConfigurationDTO.setRocketConfId(493L);
        rocketConfigurationDTO.setFullname("Space Shuttle");

        this.rocketNormalDTO = new RocketNormalDTO();
        this.rocketNormalDTO.setRocketId(453L);
        this.rocketNormalDTO.setRocketConfiguration(rocketConfigurationDTO);

        this.spacecraftConfigSummarizedDTO = new SpacecraftConfigSummarizedDTO();
        this.spacecraftConfigSummarizedDTO.setSpacecraftConfId(14L);
        this.spacecraftConfigSummarizedDTO.setSpacecraftConfName("Space Shuttle");

        this.spacecraftConfigurationDTO = new SpacecraftConfigurationDTO();
        this.spacecraftConfigurationDTO.setSpacecraftConfId(14L);
        this.spacecraftConfigurationDTO.setSpacecraftConfName("Space Shuttle");

        this.programSummarizedDTO = new ProgramSummarizedDTO();
        this.programSummarizedDTO.setProgramId(6L);
        this.programSummarizedDTO.setName("Space Shuttle");

        this.programDetailedDTO = new ProgramDetailedDTO();
        this.programDetailedDTO.setProgramId(6L);
        this.programDetailedDTO.setName("Space Shuttle");
        this.programDetailedDTO.setUpcomingLaunches(launchNormalDTO);

        this.agencyDetailedDTO = new AgencyDetailedDTO();
        this.agencyDetailedDTO.setAgencyId(44L);
        this.agencyDetailedDTO.setAgencyName("NASA");
        this.agencyDetailedDTO.setTypeName("Government");
        this.agencyDetailedDTO.setUpcomingLaunches(launchNormalDTO);

        this.launchPadDTO = new LaunchPadDTO();
        this.launchPadDTO.setLaunchPadId(87L);
        this.launchPadDTO.setName("Launch Complex 39A");
        this.launchPadDTO.setActive(true);

        this.launchPadDetailedDTO = new LaunchPadDetailedDTO();
        this.launchPadDetailedDTO.setLaunchPadId(87L);
        this.launchPadDetailedDTO.setLaunchPadName("Launch Complex 39A");
        this.launchPadDetailedDTO.setUpcomingLaunches(launchNormalDTO);

        this.launcherDTO = new LauncherDTO();
        this.launcherDTO.setLauncherId(3L);
        this.launcherDTO.setSerialNumber("F1 B0001");

        FiltersDTO nasaFilterDTO = new FiltersDTO();
        nasaFilterDTO.setFilterId(1L);
        nasaFilterDTO.setFilterName("NASA");

        FiltersDTO esaFilterDTO = new FiltersDTO();
        nasaFilterDTO.setFilterId(2L);
        nasaFilterDTO.setFilterName("ESA");

        FiltersDTO starbaseFilterDTO = new FiltersDTO();
        nasaFilterDTO.setFilterId(3L);
        nasaFilterDTO.setFilterName("Starbase");


        Map<String, List<FiltersDTO>> launchFiltersMap = new HashMap<>();
        launchFiltersMap.put("agencies", List.of(nasaFilterDTO, esaFilterDTO));
        launchFiltersMap.put("locations", List.of(starbaseFilterDTO));

        this.launchFilters = new HashMap<>();
        this.launchFilters.put("data", launchFiltersMap);

        FiltersDTO statusActiveDTO = new FiltersDTO();
        statusActiveDTO.setFilterId(1L);
        statusActiveDTO.setFilterName("Active");

        FiltersDTO natGreeceDTO = new FiltersDTO();
        natGreeceDTO.setFilterId(2L);
        natGreeceDTO.setFilterName("Greece");

        FiltersDTO natAmericanDTO = new FiltersDTO();
        natAmericanDTO.setFilterId(3L);
        natAmericanDTO.setFilterName("American");

        Map<String, Object> astronautsFiltersMap = new HashMap<>();
        astronautsFiltersMap.put("astronaut_status", List.of(statusActiveDTO));
        astronautsFiltersMap.put("nationality", List.of(natGreeceDTO, natAmericanDTO));
        this.astronautFilters = new HashMap<>();
        this.astronautFilters.put("data", astronautsFiltersMap);

        this.nasaApodDTO =
                NasaApodDTO.builder()
                        .copyright("Satoru Murata; Text: Keighley Rockcliffe (NASA GSFC, UMCP, CRESST II)")
                        .date(LocalDate.of(2026, 03, 05))
                        .explanation("rlier this week, Earth’s shadow swept across the full Moon in the year’s only total lunar eclipse. This stunning sequence combines images showing the Moon’s path across the night sky.  Each lunar image captures our planet’s shadow gradually engulfing the Moon, culminating in its red glow.  Sunlight scatters and refracts as it passes through Earth’s atmosphere toward the Moon. Shorter wavelength light (blue and green) scatters more efficiently, leaving red, orange, and yellow hues to paint the lunar surface. Tsé Bit'a'í (”rock with wings”, also known as Shiprock), located in Navajo Nation, provides a powerful volcanic foreground central to this photo and to stories of Navajo origin, adventure, and heroism. As the first full moon of the lunar new year, this eclipse held significance across cultures. Visible from East Asia to North America, this eclipse united observers across great distances, a cosmic reminder that we share the same sky.")
                        .hdurl("https://apod.nasa.gov/apod/image/2603/EclipseSequence_Murata_1080.jpg")
                        .media_type("image")
                        .title("Total Lunar Eclipse over Tsé Bit'a'í")
                        .url("https://apod.nasa.gov/apod/image/2603/EclipseSequence_Murata_1080.jpg")
                        .build();
    }

    @Nested
    @DisplayName("Public: /nasa/apod")
    class NasaApodEndpointTest {
        @Test
        @DisplayName("Should return a JSON response with the latest nasa apo picture of the day")
        public void shouldReturnNasaApodImageOfTheDay() throws Exception {
            // given
            when(nasaApodService.getNasaApodFromCache())
                    .thenReturn(nasaApodDTO);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/nasa/apod")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.copyright").value("Satoru Murata; Text: Keighley Rockcliffe (NASA GSFC, UMCP, CRESST II)"))
                    .andExpect(jsonPath("$.explanation").value("rlier this week, Earth’s shadow swept across the full Moon in the year’s only total lunar eclipse. This stunning sequence combines images showing the Moon’s path across the night sky.  Each lunar image captures our planet’s shadow gradually engulfing the Moon, culminating in its red glow.  Sunlight scatters and refracts as it passes through Earth’s atmosphere toward the Moon. Shorter wavelength light (blue and green) scatters more efficiently, leaving red, orange, and yellow hues to paint the lunar surface. Tsé Bit'a'í (”rock with wings”, also known as Shiprock), located in Navajo Nation, provides a powerful volcanic foreground central to this photo and to stories of Navajo origin, adventure, and heroism. As the first full moon of the lunar new year, this eclipse held significance across cultures. Visible from East Asia to North America, this eclipse united observers across great distances, a cosmic reminder that we share the same sky.")) //2026-03-05
                    .andExpect(jsonPath("$.hdurl").value("https://apod.nasa.gov/apod/image/2603/EclipseSequence_Murata_1080.jpg"))
                    .andExpect(jsonPath("$.media_type").value("image"))
                    .andExpect(jsonPath("$.title").value("Total Lunar Eclipse over Tsé Bit'a'í"));
        }
    }

    @Nested
    @DisplayName("Public: /launches and /launch/{id}")
    class LaunchesEndpointsTests {
        @Test
        @DisplayName("Should return a paged JSON response of launches with the requested query parameters")
        public void shouldReturnPagedLaunches() throws Exception {
            // given
            Page<DTOEntity> launchPage = new PageImpl<>(List.of(testLaunch));

            when(launchService.searchLaunch(any(), any()))
                    .thenReturn(launchPage);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/launches")
                    .param("page", "0")
                    .param("limit", "12")
                    .param("upcoming", "false")
                    .param("astronaut", "274")
                    .param("agency", "44")
                    .param("pad", "87")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$._embedded.launchDTOes[0].id").value("e1b6d391-fa37-47a5-9a18-7b19a8a183d8"))
                    .andExpect(jsonPath("$._embedded.launchDTOes[0].fullname").value("Space Shuttle Atlantis / OV-104 | STS-135"))
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andExpect(jsonPath("$.page.size").value(1));
        }

        @Test
        @DisplayName("Should return a json response with requested launch id")
        public void ShouldFindLaunchById() throws Exception {
            // given
            String launchId = "e1b6d391-fa37-47a5-9a18-7b19a8a183d8";
            when(launchService.getLaunchById(launchId))
                    .thenReturn(testLaunch);
            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/launch/{id}", launchId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(launchId))
                    .andExpect(jsonPath("$.fullname").value("Space Shuttle Atlantis / OV-104 | STS-135"));
        }

        @Test
        @DisplayName("Should return 404 when launch is not found")
        public void shouldReturn404WhenLaunchNotFound() throws Exception {
            // given
            String invalidLaunchId = "00441721-5019-4c49-aa85-e38aad2d39370";

            when(launchService.getLaunchById(invalidLaunchId))
                    .thenThrow(new ResourceNotFoundException("Launch not found"));

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/launch/{id}", invalidLaunchId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Public: /astronauts and /astronaut/{id}")
    class AstronautsEndpointsTests {
        @Test
        @DisplayName("Should return a paged JSON response of astronauts with the requested query parameters")
        public void shouldReturnPagedAstronauts() throws Exception {
            // given
            Integer astronautId = 274;
            Page<DTOEntity> launchPage = new PageImpl<>(List.of(astronautNormalDTO));

            when(astronautService.searchAstronaut(any(), any()))
                    .thenReturn(launchPage);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/astronauts")
                    .param("page", "0")
                    .param("limit", "12")
                    .param("nationality", "5")
                    .param("status", "2")
                    .param("search", "Yury Usachov")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$._embedded.astronautNormalDTOes[0].id").value(astronautId))
                    .andExpect(jsonPath("$._embedded.astronautNormalDTOes[0].name").value("Yury Usachov"))
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andExpect(jsonPath("$.page.size").value(1));
        }

        @Test
        @DisplayName("Should return a json response with requested astronaut id")
        public void ShouldFindAstronautById() throws Exception {
            // given
            Integer astronaut = 274;
            when(astronautService.getAstronautById(astronaut))
                    .thenReturn(astronautDetailedDTO);
            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/astronaut/{id}", astronaut)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(astronaut))
                    .andExpect(jsonPath("$.name").value("Yury Usachov"));
        }

        @Test
        @DisplayName("Should return 404 when astronaut is not found")
        public void shouldReturn404WhenAstronautNotFound() throws Exception {
            // given
            Integer invalidAstronautId = 274456;

            when(astronautService.getAstronautById(invalidAstronautId))
                    .thenThrow(new ResourceNotFoundException("Astronaut not found"));

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/astronaut/{id}", invalidAstronautId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Public: /rockets and /rocket/{id}")
    class RocketEndpointsTests {
        @Test
        @DisplayName("Should return a paged JSON response of rockets with the requested query parameters")
        public void shouldReturnPagedRockets() throws Exception {
            // given
            Integer rocketId = 493;
            Page<DTOEntity> rocketPage = new PageImpl<>(List.of(rocketConfigSummarizedDTO));

            when(rocketService.searchRocket(any(), any()))
                    .thenReturn(rocketPage);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/rockets")
                    .param("page", "0")
                    .param("limit", "12")
                    .param("field", "maidenFlight")
                    .param("search", "Space Shuttle")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$._embedded.rocketConfigSummarizedDTOes[0].id").value(rocketId))
                    .andExpect(jsonPath("$._embedded.rocketConfigSummarizedDTOes[0].fullname").value("Space Shuttle"))
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andExpect(jsonPath("$.page.size").value(1));
        }

        @Test
        @DisplayName("Should return a json response with requested rocket id")
        public void ShouldFindRocketById() throws Exception {
            // given
            Integer rocketId = 453;
            when(rocketService.getRocketById(rocketId))
                    .thenReturn(rocketNormalDTO);
            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/rocket/{id}", rocketId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(rocketId))
                    .andExpect(jsonPath("$.configuration.fullname").value("Space Shuttle"));
        }

        @Test
        @DisplayName("Should return 404 when rocket is not found")
        public void shouldReturn404WhenRocketNotFound() throws Exception {
            // given
            Integer invalidRocketId = 274456;

            when(rocketService.getRocketById(invalidRocketId))
                    .thenThrow(new ResourceNotFoundException("Rocket not found"));

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/rocket/{id}", invalidRocketId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Public: /spacecraft and /spacecraft/{id}")
    class SpacecraftEndpointsTests {
        @Test
        @DisplayName("Should return a paged JSON response of spacecraft with the requested query parameters")
        public void shouldReturnPagedSpacecraft() throws Exception {
            // given
            Page<DTOEntity> spacecraftPage = new PageImpl<>(List.of(spacecraftConfigSummarizedDTO));

            when(spacecraftService.searchSpacecraft(any(), any()))
                    .thenReturn(spacecraftPage);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/spacecraft")
                    .param("page", "0")
                    .param("limit", "12")
                    .param("field", "maidenFlight")
                    .param("ordering", "desc")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$._embedded.spacecraftConfigSummarizedDTOes[0].id").value("14"))
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andExpect(jsonPath("$.page.size").value(1));
        }

        @Test
        @DisplayName("Should return a json response with requested rocket id")
        public void ShouldFindSpacecraftConfigById() throws Exception {
            // given
            Integer spacecraftId = 14;
            when(spacecraftService.getSpacecraftById(spacecraftId))
                    .thenReturn(spacecraftConfigurationDTO);
            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/spacecraft/{id}", spacecraftId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(spacecraftId));
        }

        @Test
        @DisplayName("Should return 404 when rocket is not found")
        public void shouldReturn404WhenSpacecraftConfigNotFound() throws Exception {
            // given
            Integer invalidRocketId = 274456;

            when(spacecraftService.getSpacecraftById(invalidRocketId))
                    .thenThrow(new ResourceNotFoundException("Rocket not found"));

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/spacecraft/{id}", invalidRocketId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Public: /programs and /program/{id}")
    class ProgramEndpointsTests {
        @Test
        @DisplayName("Should return a paged JSON response of programs with the requested query parameters")
        public void shouldReturnPagedPrograms() throws Exception {
            // given
            Integer programId = 6;

            Page<DTOEntity> programsPage = new PageImpl<>(List.of(programSummarizedDTO));

            when(programsService.searchProgram(any(), any()))
                    .thenReturn(programsPage);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/programs")
                    .param("page", "0")
                    .param("limit", "12")
                    .param("field", "startDate")
                    .param("ordering", "desc")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$._embedded.programSummarizedDTOes[0].id").value(programId))
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andExpect(jsonPath("$.page.size").value(1));
        }

        @Test
        @DisplayName("Should return a json response with requested program id")
        public void ShouldFindProgramById() throws Exception {
            // given
            Integer programId = 6;
            String launchId = "e1b6d391-fa37-47a5-9a18-7b19a8a183d8";
            when(programsService.getProgramById(programId))
                    .thenReturn(programDetailedDTO);
            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/program/{id}", programId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(programId))
                    .andExpect(jsonPath("$.upcoming_launch.id").value(launchId));
        }

        @Test
        @DisplayName("Should return 404 when program is not found")
        public void shouldReturn404WhenProgramNotFound() throws Exception {
            // given
            Integer invalidProgramId = 274456;

            when(programsService.getProgramById(invalidProgramId))
                    .thenThrow(new ResourceNotFoundException("Program not found"));

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/program/{id}", invalidProgramId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Public: /agencies and /agency/{id}")
    class AgencyEndpointsTests {
        @Test
        @DisplayName("Should return JSON response with all featured agencies")
        public void shouldReturnAllFeaturedAgencies() throws Exception {
            // given
            Integer agencyId = 44;
            String agencyName = "NASA";
            String agencyType = "Government";

            when(agenciesService.getAllAgencies())
                    .thenReturn(List.of(agencySummarizedDTO));

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/agencies")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.data[0].id").value(agencyId))
                    .andExpect(jsonPath("$.data[0].name").value(agencyName))
                    .andExpect(jsonPath("$.data[0].type").value(agencyType));
        }

        @Test
        @DisplayName("Should return a json response with requested agency id")
        public void ShouldFindAgencyById() throws Exception {
            // given
            Integer agencyId = 44;
            String launchId = "e1b6d391-fa37-47a5-9a18-7b19a8a183d8";
            when(agenciesService.getAgencyById(agencyId))
                    .thenReturn(agencyDetailedDTO);
            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/agency/{id}", agencyId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(agencyId))
                    .andExpect(jsonPath("$.upcoming_launch.id").value(launchId));
        }

        @Test
        @DisplayName("Should return 404 when agency is not found")
        public void shouldReturn404WhenAgencyNotFound() throws Exception {
            // given
            Integer invalidAgencyId = 274456;

            when(agenciesService.getAgencyById(invalidAgencyId))
                    .thenThrow(new ResourceNotFoundException("Agency not found"));

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/agency/{id}", invalidAgencyId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Public: /launch-pads and /launch-pad/{id}")
    class LaunchPadEndpointsTests {
        @Test
        @DisplayName("Should return JSON response with all active and inactive launchPads")
        public void shouldReturnAllLaunchPads() throws Exception {
            // given
            String padId = "87";
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("active", 1);
            map.put("inactive", 0);
            map.put("pads", List.of(launchPadDTO));

            when(launchPadService.getAllLaunchPads())
                    .thenReturn(map);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/launch-pads")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.data.active").value(1))
                    .andExpect(jsonPath("$.data.inactive").value(0))
                    .andExpect(jsonPath("$.data.pads[0].id").value(padId))
                    .andExpect(jsonPath("$.data.pads[0].active").value(true));
        }

        @Test
        @DisplayName("Should return a json response with requested launchPad id")
        public void ShouldFindLaunchPadById() throws Exception {
            // given
            Integer launchPadId = 87;
            String launchId = "e1b6d391-fa37-47a5-9a18-7b19a8a183d8";
            when(launchPadService.getLaunchPadById(launchPadId))
                    .thenReturn(launchPadDetailedDTO);
            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/launch-pad/{id}", launchPadId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(launchPadId))
                    .andExpect(jsonPath("$.name").value("Launch Complex 39A"))
                    .andExpect(jsonPath("$.upcoming_launch.id").value(launchId));
        }

        @Test
        @DisplayName("Should return 404 when launchPad is not found")
        public void shouldReturn404WhenLaunchPadNotFound() throws Exception {
            // given
            Integer invalidAgencyId = 274456;

            when(launchPadService.getLaunchPadById(invalidAgencyId))
                    .thenThrow(new ResourceNotFoundException("LaunchPad not found"));

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/launch-pad/{id}", invalidAgencyId)
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Public: /launchers")
    class LaunchersPadEndpointsTests {
        /**TODO
         * extend the database with data about launchers
         * **/
        @Test
        @DisplayName("Should return a paged JSON response of launchers with the requested query parameters")
        public void shouldReturnPagedLaunchers() throws Exception {
            // given
            Integer launcherId = 3;
            String serialNumber = "F1 B0001";
            Page<DTOEntity> launchersPage = new PageImpl<>(List.of(launcherDTO));

            when(launcherService.searchLauncher(any(), any()))
                    .thenReturn(launchersPage);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/launchers")
                    .param("page", "0")
                    .param("limit", "12")
                    .param("field", "serialNumber")
                    .param("ordering", "asc")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$._embedded.launcherDTOes[0].id").value(launcherId))
                    .andExpect(jsonPath("$._embedded.launcherDTOes[0].serial_number").value(serialNumber))
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andExpect(jsonPath("$.page.size").value(1));
        }

    }

    @Nested
    @DisplayName("Public: /launches/filters and /astronauts/filters")
    class FiltersTests {
        @Test
        @DisplayName("Should fetch all available launch filters as JSON")
        public void shouldReturnLaunchFilters() throws Exception {
            when(filtersService.getLaunchFilters()).thenReturn(launchFilters);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/launches/filters")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"));
        }

        @Test
        @DisplayName("Should fetch all available astronaut filters as JSON")
        public void shouldReturnAstronautsFilters() throws Exception {
            when(filtersService.getAstronautFilters()).thenReturn(astronautFilters);

            //when & then
            RequestBuilder request = MockMvcRequestBuilders
                    .get("/public/astronauts/filters")
                    .accept(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType("application/json"));
        }
    }
}