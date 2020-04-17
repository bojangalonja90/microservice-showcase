package com.example.reactive.books.web;

import com.example.reactive.books.model.Volume;
import com.example.reactive.books.service.VolumeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.example.reactive.books.web.VolumeController.BASE_URL;

@WebFluxTest
public class VolumeControllerTest {

    private final WebTestClient client;

    static List<Volume> mockedVolumes = new ArrayList<>();

    @BeforeAll
    public static void initMockingData() {
        mockedVolumes.add(new Volume("1", "first", new ArrayList()));
    }


    @MockBean
    private VolumeService volumeService;

    @Autowired
    public VolumeControllerTest(WebTestClient client) {
        this.client = client;
    }

    @Test
    public void getAll() {
        Mockito.when(this.volumeService.findAll())//
                .thenReturn(Flux.fromIterable(mockedVolumes));

        this.client//
                .get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    public void update() {

        Volume updatedVolume = new Volume("123",  "updatedTitle", List.of("first updated author"));

        Mockito.when(this.volumeService.findById(Mockito.anyString()))//
                .thenReturn(Mono.just(mockedVolumes.get(0)));

        Mockito.when(this.volumeService.update(Mockito.anyString(),Mockito.any(Volume.class)))//
                .thenReturn(Mono.just(updatedVolume));

        this.client//
                .put()
                .uri(BASE_URL + "/123").body(Mono.just(mockedVolumes.get(0)), Volume.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo("updatedTitle")
                .jsonPath("$.authors[*]").isEqualTo("first updated author");

    }



}
