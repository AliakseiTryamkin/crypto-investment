package com.xmcy.cryptorecommendation.exception.controller;

import com.xmcy.cryptorecommendation.exception.DevCustomCryptoException;
import com.xmcy.cryptorecommendation.exception.ErrorCustomResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler.
 */
@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(value = DevCustomCryptoException.class)
    public ResponseEntity<Object> exception(DevCustomCryptoException exception) {
        return new ResponseEntity<>(createErrorCustomResponseEntity(exception), HttpStatus.NOT_FOUND);
    }

    private ErrorCustomResponseEntity createErrorCustomResponseEntity(Exception exception) {
        return ErrorCustomResponseEntity.builder()
                .message(exception.getMessage())
                .build();
    }
}
