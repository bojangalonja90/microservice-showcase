package com.example.reactive.books.config;

import com.example.reactive.books.model.Volume;
import com.example.reactive.books.repository.VolumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final VolumeRepository volumeRepository;

    public DataInitializer(VolumeRepository volumeRepository) {
        this.volumeRepository = volumeRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        List<String> authors = List.of("First Last", "Firstname2 Lastname2");
        volumeRepository.deleteAll()//
        .thenMany(
                Flux.just("title1", "title2", "title3")
                .map(name -> new Volume(UUID.randomUUID().toString(), name, authors))
                .flatMap(volumeRepository::save)
        ).thenMany(volumeRepository.findAll()).subscribe(volume -> log.info("inserted " + volume));

    }
}
