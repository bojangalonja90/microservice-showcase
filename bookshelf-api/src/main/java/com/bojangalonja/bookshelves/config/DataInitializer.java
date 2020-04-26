package com.bojangalonja.bookshelves.config;

import com.bojangalonja.bookshelves.model.Bookshelf;
import com.bojangalonja.bookshelves.repository.BookshelfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final BookshelfRepository bookshelfRepository;

    public DataInitializer(BookshelfRepository bookshelfRepository) {
        this.bookshelfRepository = bookshelfRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        List<Bookshelf> bookshelves = List.of(
                new Bookshelf("1", "1", "To read", //
                        "Books to read", new ArrayList<>(), new Date(), new Date(), 0),
                new Bookshelf("2", "2", "To read", //
                        "Books to read", new ArrayList<>(), new Date(), new Date(), 0)
        );

        bookshelfRepository.deleteAll().thenMany(Flux.fromIterable(bookshelves))
                .flatMap(bookshelfRepository::save).thenMany(bookshelfRepository.findAll())//
                .subscribe(bookshelf -> log.info("Inserted: " + bookshelf));

    }
}
