package com.example.reactive.books.web;

import com.example.reactive.books.exceptions.BadRequestException;
import com.example.reactive.books.model.ErrorResponse;
import com.example.reactive.books.model.Volume;
import com.example.reactive.books.service.VolumeService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(VolumeController.BASE_URL)
@CrossOrigin(origins = {"http://localhost:8080", "http://gateway:8080"})
public class VolumeController {

    public static final String BASE_URL = "/api/v1/volumes";

    private final VolumeService volumeService;

    @Autowired
    public VolumeController(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    @GetMapping()
    public Flux<Volume> findAllBooks(){
        return this.volumeService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Volume> findOne(@PathVariable String id) {
        return this.volumeService.findById(id);
    }

    @PostMapping()
    public Mono<Volume> create(@RequestBody Volume volume) {
        return volumeService.create(volume);
    }

    @PutMapping("/{id}")
    public Mono<Volume> update(@PathVariable String id, @RequestBody Volume volume) {
        return volumeService.update(id, volume);
    }



}
