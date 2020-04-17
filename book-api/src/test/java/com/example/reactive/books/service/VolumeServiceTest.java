package com.example.reactive.books.service;

import com.example.reactive.books.exceptions.BadRequestException;
import com.example.reactive.books.exceptions.NoDataFoundException;
import com.example.reactive.books.model.Volume;
import com.example.reactive.books.repository.VolumeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class VolumeServiceTest {

    @Mock
    private VolumeRepository volumeRepository;

    @InjectMocks
    private VolumeService volumeService;

    static List<Volume> mockedVolumes = new ArrayList<>();

    @BeforeAll
    public static void initMockingData() {
        mockedVolumes.add(new Volume("1", "first", new ArrayList()));
    }

    @Test
    void shouldInjectMocks() {
        assertThat(volumeRepository).isNotNull();
        assertThat(volumeService).isNotNull();
    }

    @Test
    public void testFindAll_success() {
        Mockito.when(this.volumeRepository.findAll())//
                .thenReturn(Flux.fromIterable(mockedVolumes));
        Flux<Volume> foundAll = volumeService.findAll();
        Predicate<Volume> matched = volume -> foundAll.any(savedItem -> savedItem.getId().equals(volume.getId())).block();

        StepVerifier.create(foundAll)
                .expectNextMatches(matched)
                .verifyComplete();
    }

    @Test
    public void testFindbyId_success() {
        String id = "1";
        Mockito.when(this.volumeRepository.findById(id)).thenReturn(Mono.just(mockedVolumes.get(0)));
        Mono<Volume> found = volumeService.findById(id).flatMap(v -> Mono.just(v));

        StepVerifier.create(found)
                .expectNextMatches(volume -> id.equals(volume.getId()))
                .verifyComplete();

    }

    @Test
    public void testCreate_success() {
        String id = "2";
        Volume newVolumeMocked = new Volume(id, "name", new ArrayList<>());
        Mockito.when(this.volumeRepository.save(Mockito.any(Volume.class)))//
                .thenReturn(Mono.just(newVolumeMocked));
        Mono<Volume> created = volumeService.create(new Volume(id, "name", new ArrayList<>()))//
                .flatMap(v -> Mono.just(v));

        StepVerifier.create(created)
                .expectNextMatches(volume -> id.equals(volume.getId()))
                .verifyComplete();

    }

    @Test
    public void testUpdate_success() {
        String id = "1";
        String nameUpdated = "nameUpdated";
        List<String> updatedAuthors = List.of("author updated");

        Mockito.when(this.volumeRepository.findById(id))//
                .thenReturn(Mono.just(mockedVolumes.get(0)));

        ArgumentCaptor<Volume> volumeCaptor = ArgumentCaptor.forClass(Volume.class);

        Volume newVolumeMocked = new Volume(id, nameUpdated, updatedAuthors);
        Mockito.when(this.volumeRepository.save(Mockito.any(Volume.class)))//
                .thenReturn(Mono.just(newVolumeMocked));
        Mono<Volume> created = volumeService.update(id, new Volume(id, nameUpdated, updatedAuthors))//
                .flatMap(v -> Mono.just(v));


        StepVerifier.create(created)
                .expectNextMatches(volume -> id.equals(volume.getId()))
                .verifyComplete();

        Mockito.verify(volumeRepository).save(volumeCaptor.capture());

        Volume volumeCaptured = volumeCaptor.getValue();
        assertEquals(volumeCaptured.getId(), id);
        assertEquals(volumeCaptured.getTitle(), nameUpdated);
        assertEquals(volumeCaptured.getAuthors().get(0), updatedAuthors.get(0));
    }

    @Test
    public void testUpdate_wrongId_shouldThrowException() {
        String id = "1";
        String name = "nameUpdated";

        Mockito.when(this.volumeRepository.findById(id))//
                .thenReturn(Mono.empty());

        Mono<Volume> created = volumeService.update(id, new Volume(id, name, new ArrayList<>()))//
                .flatMap(v -> Mono.just(v));

        StepVerifier.create(created)
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    public void testDelete_success() {
        String id = "1";

        Mockito.when(this.volumeRepository.findById(id))//
                .thenReturn(Mono.just(mockedVolumes.get(0)));

        Mockito.when(this.volumeRepository.delete(Mockito.any(Volume.class)))//
                .thenReturn(Mono.empty());
        Mono<Void> deleted = volumeService.delete(id)//
                .flatMap(v -> Mono.just(v));

        StepVerifier.create(deleted)
                .expectNext()
                .verifyComplete();

    }

    @Test
    public void testDelete_wrongId_shouldThrowException() {
        String id = "1";

        Mockito.when(this.volumeRepository.findById(id))//
                .thenReturn(Mono.empty());

        Mono<Void> deleted = volumeService.delete(id)//
                .flatMap(v -> Mono.just(v));

        StepVerifier.create(deleted)
                .expectError(BadRequestException.class)
                .verify();

    }

}
