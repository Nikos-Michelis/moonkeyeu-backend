package com.moonkeyeu.core.api.launch.integration.controller;
import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8081"
)
@Import({TestSecurityConfiguration.class, TestContainerConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PublicControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnNasaPictureOfTheDay() {
        webTestClient.get()
                .uri("/public/nasa/apod")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    void shouldReturnLaunchById() {
        String launchId = "bf08a10b-35f0-4736-97f3-ba111e59cd55";
        webTestClient.get()
                .uri("/public/launch/{id}", launchId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(launchId);
    }

    @Test
    void shouldReturnLaunchesByQueryParams() {
        webTestClient.get()
                .uri("/public/launches?page=0&limit=12&upcoming=false&agency=191&program=6&pad=87")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$._embedded.launchNormalDTOes[0].id").isEqualTo("e1b6d391-fa37-47a5-9a18-7b19a8a183d8");
    }

    @Test
    void shouldReturnAstronautById() {
        String astronaut = "274";
        webTestClient.get()
                .uri("/public/astronaut/{id}", astronaut)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(astronaut);

    }

    @Test
    void shouldReturnAstronautsByQueryParams() {
        webTestClient.get()
                .uri("/public/astronauts?page=0&limit=12&ordering=asc&nationality=5&status=2&search=Yury+Usachov")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$._embedded.astronautNormalDTOes[0].name").isEqualTo("Yury Usachov");
    }


    @Test
    void shouldReturnRocketById() {
        String rocket = "453";
        webTestClient.get()
                .uri("/public/rocket/{id}", rocket)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(rocket);
    }

    @Test
    void shouldReturnRocketsByQueryParams() {
        webTestClient.get()
                .uri("/public/astronauts?page=0&limit=12&ordering=desc")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnSpacecraftById() {
        String spacecraft = "14";
        webTestClient.get()
                .uri("/public/spacecraft/{id}", spacecraft)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(spacecraft);

    }

    @Test
    void shouldReturnSpacecraftsByQueryParams() {
        webTestClient.get()
                .uri("/public/spacecraft?page=0&limit=12&ordering=desc")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnProgramById() {
        String program = "6";
        webTestClient.get()
                .uri("/public/program/{id}", program)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(program);

    }

    @Test
    void shouldReturnProgramsByQueryParams() {
        webTestClient.get()
                .uri("/public/programs?page=0&limit=12&ordering=desc")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnAgencyById() {
        String agency = "44";
        webTestClient.get()
                .uri("/public/agency/{id}", agency)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(agency);

    }

    @Test
    void shouldReturnAllFeaturedAgencies() {
        webTestClient.get()
                .uri("/public/agencies")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnLaunchPadById() {
        String pad = "87";
        webTestClient.get()
                .uri("/public/launch-pad/{id}", pad)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(pad);

    }

    @Test
    void shouldReturnAllLaunchPads() {
        webTestClient.get()
                .uri("/public/launch-pads")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnAllLaunchers() {
        webTestClient.get()
                .uri("/public/launchers?page=0&limit=12&ordering=desc")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnLaunchFilters() {
        webTestClient.get()
                .uri("/public/launches/filters")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    void shouldAstronautFilters() {
        webTestClient.get()
                .uri("/public/astronauts/filters")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println);
    }

}
