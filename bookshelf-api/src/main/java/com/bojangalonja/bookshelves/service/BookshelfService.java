package com.bojangalonja.bookshelves.service;

import com.bojangalonja.bookshelves.client.VolumesApiClient;
import com.bojangalonja.bookshelves.exceptions.BadRequestException;
import com.bojangalonja.bookshelves.mapper.BookshelfMapper;
import com.bojangalonja.bookshelves.model.Bookshelf;
import com.bojangalonja.bookshelves.model.BookshelfDTO;
import com.bojangalonja.bookshelves.repository.BookshelfRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookshelfService {

    private final BookshelfMapper bookshelfMapper;

    private final BookshelfRepository bookshelfRepository;

    private final VolumesApiClient volumesApiClient;


    public BookshelfService(BookshelfMapper bookshelfMapper, BookshelfRepository bookshelfRepository, VolumesApiClient volumesApiClient){
        this.bookshelfMapper = bookshelfMapper;
        this.bookshelfRepository = bookshelfRepository;
        this.volumesApiClient = volumesApiClient;
    }

    public Flux<BookshelfDTO> findAll(String userId) {
        return bookshelfRepository.findAllByUserId(userId).map(bookshelf -> bookshelfMapper.bookshelfToBookshelfDto(bookshelf))
                .collect(Collectors.toList())
                .flatMapMany(bookshelfDTOs -> Flux.fromIterable(bookshelfDTOs));
    }

    public Mono<BookshelfDTO> findOne(String userId, String id) {
        return bookshelfRepository.findByUserIdAndId(userId, id).log()//
                .flatMap(bookshelf -> Mono.just(bookshelfMapper.bookshelfToBookshelfDto(bookshelf)));
    }

    public Mono<BookshelfDTO> create(String userId, BookshelfDTO bookshelfDTO) {

        return getValidVolumeIds(bookshelfDTO.getItems()).zipWhen(validIds -> {
            bookshelfDTO.setItems(validIds);
            //userExists(bookshelfDTO.getUserId());
            bookshelfDTO.setUserId(userId);
            return Mono.just(bookshelfDTO);
        }).flatMap(tuple -> {
            Bookshelf bookshelf = bookshelfMapper.bookshelfDtoToBookshelf(bookshelfDTO);
            return bookshelfRepository.save(bookshelf);
        }).flatMap(b -> Mono.just(bookshelfMapper.bookshelfToBookshelfDto(b)));

    }


    public Mono<BookshelfDTO> update(String userId, String id, BookshelfDTO bookshelfDTO) {
        return bookshelfRepository.findByUserIdAndId(userId, id).switchIfEmpty(Mono.error(
                new BadRequestException(String.format("The bookshelf with id: %s and user: %s not found ", id, userId))))
                .zipWhen(foundBookshelf -> {
                    return prepareBookshelfToPersist(foundBookshelf, bookshelfDTO);
                })
                .zipWhen(tuple2 -> {
                    return getValidVolumeIds(bookshelfDTO.getItems());
                })
                .flatMap(tupl -> {
                    Bookshelf bookshelfToSave = tupl.getT1().getT2();
                    List<String> validBookIds = tupl.getT2();
                    bookshelfToSave.setItems(validBookIds);

                    return bookshelfRepository.save(bookshelfToSave);
                })
                .flatMap(bookshelfSaved -> Mono.just(bookshelfMapper.bookshelfToBookshelfDto(bookshelfSaved)));

    }

    private Mono<Bookshelf> prepareBookshelfToPersist(Bookshelf original, BookshelfDTO toUpdate){

        Bookshelf bookshelfMapped = bookshelfMapper.bookshelfDtoToBookshelf(toUpdate);
        bookshelfMapped.setId(original.getId());
        bookshelfMapped.setUserId(original.getUserId());

        return Mono.just(bookshelfMapped);

    }

    public Mono<Void> delete(String userId, String id) {
        return bookshelfRepository.findByUserIdAndId(userId, id).switchIfEmpty(Mono.error(
                    new BadRequestException(String.format("The bookshelf with id: %s and user: %s not found ", id, userId)))
                )
                .flatMap(m -> {
                    return this.bookshelfRepository.deleteByUserIdAndId(userId, id);
                });

    }

    private Mono<User> userExists(String userId) {
        //Dummy user validation implementation
        if(!"1".equals(userId)){
            return Mono.error(new BadRequestException(String.format("The user with id does not exist: %s", userId)));
        }

        return Mono.just(new User("1"));
    }

    private Mono<List<String>> getValidVolumeIds(List<String> idsToCheck) {
        return Flux.fromIterable(idsToCheck)
                .flatMap(id -> volumesApiClient.getVolume(id))
                .flatMap(volume -> Mono.just(volume.getId()))
                .collectList();

    }


}

class User{
    private String id;

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
