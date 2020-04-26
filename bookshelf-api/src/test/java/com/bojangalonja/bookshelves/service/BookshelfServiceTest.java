package com.bojangalonja.bookshelves.service;

import com.bojangalonja.bookshelves.client.Volume;
import com.bojangalonja.bookshelves.client.VolumesApiClient;
import com.bojangalonja.bookshelves.exceptions.BadRequestException;
import com.bojangalonja.bookshelves.mapper.BookshelfMapper;
import com.bojangalonja.bookshelves.mapper.BookshelfMapperImpl;
import com.bojangalonja.bookshelves.model.Bookshelf;
import com.bojangalonja.bookshelves.model.BookshelfDTO;
import com.bojangalonja.bookshelves.repository.BookshelfRepository;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assert;
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
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookshelfServiceTest {

    private BookshelfMapper bookshelfMapper = new BookshelfMapperImpl();

    private BookshelfRepository bookshelfRepository = Mockito.mock(BookshelfRepository.class);

    private VolumesApiClient volumesApiClient = Mockito.mock(VolumesApiClient.class);

    private BookshelfService bookshelfService =
            new BookshelfService(bookshelfMapper, bookshelfRepository, volumesApiClient);

    private List<Bookshelf> mockBookshelves = List.of(
            new Bookshelf("1", "1", "To Read", "Volumes to read", List.of("1", "2"), new Date(), new Date(), 2),
            new Bookshelf("2", "1", "Already read", "Volumes already read", List.of("3", "4"), new Date(), new Date(), 2)

    );
    @BeforeEach
    public void initMockData() {

    }

    @Test
    void shouldInjectMocks() {
        assertThat(bookshelfMapper).isNotNull();
        assertThat(bookshelfRepository).isNotNull();
        assertThat(volumesApiClient).isNotNull();
    }

    @Test
    public void test_findAllByUserId_succes(){

        String userId = "1";

        Mockito.when(bookshelfRepository.findAllByUserId(userId)).thenReturn(Flux.fromIterable(mockBookshelves));

        Flux<BookshelfDTO> foundAll = bookshelfService.findAll(userId);
        Predicate<BookshelfDTO> matched = bookshelfDTO -> foundAll.any(savedItem -> savedItem.getId().equals(bookshelfDTO.getId())).block();

        StepVerifier.create(foundAll)
                .expectNextMatches(matched)
                .expectNextMatches(matched)
                .verifyComplete();

    }

    @Test
    public void test_findAllWrongUserIdReturnEmptyList_succes(){

        String userId = "2";

        Mockito.when(bookshelfRepository.findAllByUserId(userId)).thenReturn(Flux.fromIterable(new ArrayList<>()));

        Flux<BookshelfDTO> foundAll = bookshelfService.findAll(userId);

        StepVerifier.create(foundAll)
                .expectNextCount(0)
                .expectComplete();

    }

    @Test
    public void test_findByIdAndUserId_succes(){

        String id = "1";
        String userId = "1";

        Mockito.when(bookshelfRepository.findByUserIdAndId(userId, id))
                .thenReturn(Mono.just(mockBookshelves.stream()//
                        .filter(bookshelf -> id.equals(bookshelf.getId()))
                        .findFirst().get()));

        Mono<BookshelfDTO> found = bookshelfService.findOne(userId, id);

        StepVerifier.create(found)
                .expectNextMatches(bookshelfDTO -> id.equals(bookshelfDTO.getId()))
                .expectComplete();

    }

    @Test
    public void test_findByIdAndUserIdWrongId_returnEmpty(){
        String id = "1";
        String userId = "2";

        Mockito.when(bookshelfRepository.findByUserIdAndId(userId, id))
                .thenReturn(Mono.empty());

        Mono<BookshelfDTO> found = bookshelfService.findOne(userId, id);

        StepVerifier.create(found)
                .expectNextCount(0)
                .expectComplete();
    }

    @Test
    public void test_createNewBookshelf_success() {
        String id = "1";
        String userId = "1";

        String volumeOneId = "first";
        String volumeTwoId = "second";

        BookshelfDTO toCreate = new BookshelfDTO(id, userId, "New title of bookshelf",
                "", List.of(volumeOneId, volumeTwoId), new Date(), new Date(), 1);

        Mockito.when(volumesApiClient.getVolume(volumeOneId))//
                .thenReturn(Mono.just(new Volume(volumeOneId, "Title", new ArrayList<>())));

        Mockito.when(volumesApiClient.getVolume(volumeTwoId))//
                .thenReturn(Mono.just(new Volume(volumeTwoId, "Title2", new ArrayList<>())));

        Mockito.when(bookshelfRepository.save(Mockito.any(Bookshelf.class)))//
                .thenReturn(Mono.just(bookshelfMapper.bookshelfDtoToBookshelf(toCreate)));

        ArgumentCaptor<Bookshelf> bookshelfArgumentCaptor = ArgumentCaptor.forClass(Bookshelf.class);

        Mono<BookshelfDTO> created = bookshelfService.create(userId, toCreate);

        StepVerifier.create(created)
                .expectNextMatches(bookshelfDTO -> id.equals(bookshelfDTO.getId()))
                .verifyComplete();

        Mockito.verify(this.bookshelfRepository).save(bookshelfArgumentCaptor.capture());

        Bookshelf bookshelfCaptured = bookshelfArgumentCaptor.getValue();
        assertNotNull(bookshelfCaptured.getId());
        assertEquals(bookshelfCaptured.getTitle(), toCreate.getTitle());
        assertIterableEquals(bookshelfCaptured.getItems(), toCreate.getItems());

    }


    @Test
    public void test_createNewBookshelfUserDoesNotExist_throwException() {
        String id = "1";
        //non-existing user id
        String userId = "2";

        String volumeOneId = "first";
        String volumeTwoId = "second";

        BookshelfDTO toCreate = new BookshelfDTO(id, userId, "New title of bookshelf",
                "", List.of(volumeOneId, volumeTwoId), new Date(), new Date(), 1);

        Mono<BookshelfDTO> created = bookshelfService.create(userId, toCreate);

        StepVerifier.create(created)
                .expectError(BadRequestException.class);


    }

    //TODO create with non-existing volume
    @Test
    public void test_createNewBookshelfWithNonExistingVolume_success() {
        String id = "1";
        String userId = "1";

        String volumeOneId = "first";
        String volumeTwoId = "second";
        String volumeNonExisting = "non-existing";

        List<String> volumesToSave = List.of(volumeOneId, volumeTwoId, volumeNonExisting);

        BookshelfDTO toCreate = new BookshelfDTO(id, userId, "New title of bookshelf",
                "", volumesToSave, new Date(), new Date(), 1);

        Mockito.when(volumesApiClient.getVolume(volumeOneId))//
                .thenReturn(Mono.just(new Volume(volumeOneId, "Title", new ArrayList<>())));

        Mockito.when(volumesApiClient.getVolume(volumeTwoId))//
                .thenReturn(Mono.just(new Volume(volumeTwoId, "Title2", new ArrayList<>())));

        //Non-existing volume will be ignored when saving
        Mockito.when(volumesApiClient.getVolume(volumeNonExisting))//
                .thenReturn(Mono.empty());

        Mockito.when(bookshelfRepository.save(Mockito.any(Bookshelf.class)))//
                .thenReturn(Mono.just(bookshelfMapper.bookshelfDtoToBookshelf(toCreate)));

        ArgumentCaptor<Bookshelf> bookshelfArgumentCaptor = ArgumentCaptor.forClass(Bookshelf.class);

        Mono<BookshelfDTO> created = bookshelfService.create(userId, toCreate);

        StepVerifier.create(created)
                .expectNextMatches(bookshelfDTO -> id.equals(bookshelfDTO.getId()))
                .verifyComplete();

        Mockito.verify(this.bookshelfRepository).save(bookshelfArgumentCaptor.capture());
        Mockito.verify(this.volumesApiClient, Mockito.times(volumesToSave.size())).getVolume(Mockito.anyString());


        Bookshelf bookshelfCaptured = bookshelfArgumentCaptor.getValue();
        assertNotNull(bookshelfCaptured.getId());
        assertEquals(bookshelfCaptured.getTitle(), toCreate.getTitle());
        assertIterableEquals(bookshelfCaptured.getItems(), toCreate.getItems());

    }



    @Test
    public void test_updateBookshelf_success() {
        String id = "1";
        String userId = "1";

        String volumeOneId = "first";
        String volumeTwoId = "second";

        BookshelfDTO toUpdate = new BookshelfDTO(id, userId, "New title of bookshelf",
                "", List.of(volumeOneId, volumeTwoId), new Date(), new Date(), 1);

        Bookshelf existing = new Bookshelf(id, userId, "Old title of bookshelf",
                "", new ArrayList<>(), new Date(), new Date(), 0);

        Mockito.when(volumesApiClient.getVolume(volumeOneId))//
                .thenReturn(Mono.just(new Volume(volumeOneId, "Title", new ArrayList<>())));

        Mockito.when(volumesApiClient.getVolume(volumeTwoId))//
                .thenReturn(Mono.just(new Volume(volumeTwoId, "Title2", new ArrayList<>())));

        Mockito.when(bookshelfRepository.findByUserIdAndId(userId,id)).thenReturn(Mono.just(existing));

        Mockito.when(bookshelfRepository.save(Mockito.any(Bookshelf.class)))//
                .thenReturn(Mono.just(bookshelfMapper.bookshelfDtoToBookshelf(toUpdate)));

        ArgumentCaptor<Bookshelf> bookshelfArgumentCaptor = ArgumentCaptor.forClass(Bookshelf.class);

        Mono<BookshelfDTO> updated = bookshelfService.update(userId, id, toUpdate);

        StepVerifier.create(updated)
                .expectNextMatches(bookshelfDTO -> id.equals(bookshelfDTO.getId()))
                .verifyComplete();

        Mockito.verify(this.bookshelfRepository).save(bookshelfArgumentCaptor.capture());

        Bookshelf bookshelfCaptured = bookshelfArgumentCaptor.getValue();
        assertNotNull(bookshelfCaptured.getId());
        assertEquals(bookshelfCaptured.getTitle(), toUpdate.getTitle());
        assertIterableEquals(bookshelfCaptured.getItems(), toUpdate.getItems());

    }

    @Test
    public void test_updateBookshelfWithNonExistingVolume_success() {
        String id = "1";
        String userId = "1";

        String volumeOneId = "first";
        String volumeTwoId = "second";
        String volumeNonExisting = "non-existing";

        List<String> volumesToSave = List.of(volumeOneId, volumeTwoId, volumeNonExisting);

        BookshelfDTO toUpdate = new BookshelfDTO(id, userId, "New title of bookshelf",
                "", volumesToSave, new Date(), new Date(), 1);

        Bookshelf existing = new Bookshelf(id, userId, "Old title of bookshelf",
                "", new ArrayList<>(), new Date(), new Date(), 0);

        Mockito.when(volumesApiClient.getVolume(volumeOneId))//
                .thenReturn(Mono.just(new Volume(volumeOneId, "Title", new ArrayList<>())));

        Mockito.when(volumesApiClient.getVolume(volumeTwoId))//
                .thenReturn(Mono.just(new Volume(volumeTwoId, "Title2", new ArrayList<>())));

        //Non-existing volume will be ignored when saving
        Mockito.when(volumesApiClient.getVolume("non-existing"))//
                .thenReturn(Mono.empty());

        Mockito.when(bookshelfRepository.findByUserIdAndId(userId,id)).thenReturn(Mono.just(existing));

        Mockito.when(bookshelfRepository.save(Mockito.any(Bookshelf.class)))//
                .thenReturn(Mono.just(bookshelfMapper.bookshelfDtoToBookshelf(toUpdate)));

        ArgumentCaptor<Bookshelf> bookshelfArgumentCaptor = ArgumentCaptor.forClass(Bookshelf.class);

        Mono<BookshelfDTO> updated = bookshelfService.update(userId, id, toUpdate);

        StepVerifier.create(updated)
                .expectNextMatches(bookshelfDTO -> id.equals(bookshelfDTO.getId()))
                .verifyComplete();

        Mockito.verify(this.bookshelfRepository).save(bookshelfArgumentCaptor.capture());
        Mockito.verify(this.volumesApiClient, Mockito.times(volumesToSave.size())).getVolume(Mockito.anyString());

        Bookshelf bookshelfCaptured = bookshelfArgumentCaptor.getValue();
        assertNotNull(bookshelfCaptured.getId());
        assertEquals(bookshelfCaptured.getTitle(), toUpdate.getTitle());
        assertEquals(bookshelfCaptured.getItems().size(), 2);

    }

    @Test
    public void testDelete_success() {
        String id = "1";
        String userId = "1";

        Mockito.when(this.bookshelfRepository.findByUserIdAndId(userId, id))//
                .thenReturn(Mono.just(mockBookshelves.get(0)));

        Mockito.when(this.bookshelfRepository.deleteByUserIdAndId(userId, id))//
                .thenReturn(Mono.empty());
        Mono<Void> deleted = bookshelfService.delete(userId, id)//
                .flatMap(v -> Mono.just(v));

        StepVerifier.create(deleted)
                .verifyComplete();

    }

    @Test
    public void testDelete_wrongId_shouldThrowException() {
        String id = "1";
        String userId = "2";

        Mockito.when(this.bookshelfRepository.findByUserIdAndId(userId, id))//
                .thenReturn(Mono.empty());

        Mono<Void> deleted = bookshelfService.delete(userId, id)//
                .flatMap(v -> Mono.just(v));

        StepVerifier.create(deleted)
                .expectError(BadRequestException.class)
                .verify();

    }




}
