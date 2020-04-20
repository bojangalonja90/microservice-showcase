package com.bojangalonja.bookshelves.repository;

import com.bojangalonja.bookshelves.model.Bookshelf;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface BookshelfRepository extends ReactiveMongoRepository<Bookshelf, String> {

    Flux<Bookshelf> findAllByUserId(String userId);

    Mono<Bookshelf> findByUserIdAndId(String userId, String id);

    void deleteByUserIdAndId(String userId, String id);

}
