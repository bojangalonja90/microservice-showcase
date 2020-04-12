package com.example.reactive.books.repository;

import com.example.reactive.books.model.Volume;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface VolumeRepository extends ReactiveMongoRepository<Volume, String> {

}
