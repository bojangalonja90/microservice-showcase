package com.example.reactive.books.service;

import com.example.reactive.books.exceptions.BadRequestException;
import com.example.reactive.books.model.Volume;
import com.example.reactive.books.repository.VolumeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class VolumeService {

    private final VolumeRepository volumeRepository;

    public VolumeService(VolumeRepository volumeRepository) {
        this.volumeRepository = volumeRepository;
    }

    public Flux<Volume> findAll() {
        return volumeRepository.findAll();
    }

    public Mono<Volume> findById(String id) {
        return volumeRepository.findById(id);
    }

    public Mono<Volume> create(Volume volume) {
        return volumeRepository.save(volume);
    }

    public Mono<Void> delete(String id) {
        return volumeRepository.findById(id)//
                .switchIfEmpty(Mono.error(
                        new BadRequestException(String.format("The volume with id: %s not found", id)))
                )
                .flatMap(this.volumeRepository::delete);

    }

    public Mono<Volume> update(String id, Volume volume) {

        return volumeRepository.findById(id)//
                .switchIfEmpty(Mono.error(new BadRequestException(String.format("The volume with id: %s not found", id))))
                .map(v -> new Volume(v.getId(), volume.getTitle(), volume.getAuthors()))
                .flatMap(this.volumeRepository::save);
    }
}
