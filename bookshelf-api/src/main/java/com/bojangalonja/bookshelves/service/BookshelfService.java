package com.bojangalonja.bookshelves.service;

import com.bojangalonja.bookshelves.exceptions.BadRequestException;
import com.bojangalonja.bookshelves.mapper.BookshelfMapper;
import com.bojangalonja.bookshelves.model.Bookshelf;
import com.bojangalonja.bookshelves.model.BookshelfDTO;
import com.bojangalonja.bookshelves.repository.BookshelfRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookshelfService {

    private final BookshelfMapper bookshelfMapper;

    private final BookshelfRepository bookshelfRepository;

    List<Bookshelf> bookshelves = List.of(new Bookshelf("1", "1", "To read", "List of books to read", //
            List.of("123"), new Date(), new Date(), 1));


    public BookshelfService(BookshelfMapper bookshelfMapper, BookshelfRepository bookshelfRepository){
        this.bookshelfMapper = bookshelfMapper;
        this.bookshelfRepository = bookshelfRepository;
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
        //TODO validate user

        //TODO validate books

        bookshelfDTO.setUserId(userId);
        Bookshelf bookshelf = bookshelfMapper.bookshelfDtoToBookshelf(bookshelfDTO);

        return bookshelfRepository.save(bookshelf).flatMap(b -> Mono.just(bookshelfMapper.bookshelfToBookshelfDto(b)));

    }


    public Mono<BookshelfDTO> update(String userId, String id, BookshelfDTO bookshelfDTO) {
        return findOne(userId, id).switchIfEmpty(Mono.error(
                new BadRequestException(String.format("The bookshelf with id: %s and user: %s not found ", id, userId))))
                .map(b -> {

                    Bookshelf bookshelfMapped = bookshelfMapper.bookshelfDtoToBookshelf(b);
                    bookshelfMapped.setId(b.getId());
                    bookshelfMapped.setUserId(b.getUserId());
                    return bookshelfMapped;

                })
                .flatMap(bookshelf -> bookshelfRepository.save(bookshelf))
                .flatMap(bookshelfSaved -> Mono.just(bookshelfMapper.bookshelfToBookshelfDto(bookshelfSaved)));

        //TODO log errors as they happen
    }

    public void delete(String userId, String id) {
        bookshelfRepository.findByUserIdAndId(userId, id).switchIfEmpty(Mono.error(
                new BadRequestException(String.format("The bookshelf with id: %s and user: %s not found ", id, userId))));
        bookshelfRepository.deleteByUserIdAndId(userId, id);
    }

}
