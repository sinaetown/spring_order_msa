package com.encore.ordering.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerClass {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> entityNotFoundHandler(EntityNotFoundException e) {
        log.error("Handler EntityNotFoundException Message : " + e.getMessage());
        return ErrorResponseDto.makeMessage(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> IllegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("Handler IllegalArgumentException Message : " + e.getMessage());
        return ErrorResponseDto.makeMessage(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
