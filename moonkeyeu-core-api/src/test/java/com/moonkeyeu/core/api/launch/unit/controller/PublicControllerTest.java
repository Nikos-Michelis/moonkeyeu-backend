package com.moonkeyeu.core.api.launch.unit.controller;

import com.moonkeyeu.core.api.configuration.security.jwt.JwtAuthenticationFilter;
import com.moonkeyeu.core.api.launch.controller.PublicController;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchDTO;
import com.moonkeyeu.core.api.launch.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = PublicController.class
)@AutoConfigureMockMvc(addFilters = false)
class PublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
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

    @BeforeEach
    void setUp() {
        this.testLaunch = new LaunchDTO();
        this.testLaunch.setLaunchId("00441721-5019-4c49-aa85-e38aad2d3937");
    }

    private String jsonFromFile(String pathResources) throws IOException {
        ClassPathResource resource = new ClassPathResource(pathResources);
        Path jsonFilePath = resource.getFile().toPath();
        return Files.readString(jsonFilePath);
    }

    @Test
    @DisplayName("Should return a json response with requested launch id")
    public void ShouldFindLaunchById() throws Exception {
        // given
        when(launchService.getLaunchById("00441721-5019-4c49-aa85-e38aad2d3937"))
                .thenReturn(testLaunch);
        //when & then
        RequestBuilder request = MockMvcRequestBuilders
                .get("/public/launch/00441721-5019-4c49-aa85-e38aad2d3937")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonFromFile("json/launch.json"));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("00441721-5019-4c49-aa85-e38aad2d3937"));
    }
}