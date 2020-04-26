package com.example.reactive.books.web;

import com.example.reactive.books.exceptions.BadRequestException;
import com.example.reactive.books.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorHandlingContollerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlingContollerAdvice.class);

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity> handleBadRequestException(BadRequestException ex) {

        LOGGER.error("Error occurred: ", ex);

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(ex.getMessage())));
    }
}
