package com.bojangalonja.bookshelves.web;

import com.bojangalonja.bookshelves.model.BookshelfDTO;
import com.bojangalonja.bookshelves.service.BookshelfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = BookshelfController.BASE_URL)
@CrossOrigin(origins = {"http://localhost:8080", "http://gateway:8080"})
public class BookshelfController {

    public static final Logger log = LoggerFactory.getLogger(BookshelfController.class);

    public static final String BASE_URL = "/bookshelves";

    private final BookshelfService bookshelfService;

    public BookshelfController(BookshelfService bookshelfService) {
        this.bookshelfService = bookshelfService;
    }

    @GetMapping
    public Mono<ResponseEntity> getBookshelves(@RequestParam String userId) {

        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(bookshelfService.findAll(userId)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity> getBookshelves(@PathVariable String id, @RequestParam String userId) {

        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(bookshelfService.findOne(userId, id)));
    }

    @PostMapping
    public Mono<ResponseEntity> createBookshelf(@RequestBody BookshelfDTO bookshelfDTO) {
        return Mono.just(ResponseEntity.ok().body(bookshelfService.create(bookshelfDTO.getUserId(), bookshelfDTO)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity> updateBookshelf(@PathVariable String id, @RequestParam  String userId, @RequestBody BookshelfDTO bookshelfDTO) {
        return Mono.just(ResponseEntity.ok().body(bookshelfService.update(userId, id, bookshelfDTO)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity> deleteBookshelf(@PathVariable String id, @RequestParam String userId ) {
        bookshelfService.delete(userId,id);
        return Mono.just(ResponseEntity.ok().build());
    }

}




