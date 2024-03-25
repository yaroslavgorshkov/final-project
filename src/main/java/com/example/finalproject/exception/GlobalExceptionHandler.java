package com.example.finalproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppError> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AppError> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppError> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AppError> handleBadCredentials(BadCredentialsException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomUserHasNotFoundException.class)
    public ResponseEntity<AppError> handleCustomUserHasNotFound(CustomUserHasNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomTaskHasNotFoundException.class)
    public ResponseEntity<AppError> handleCustomTaskHasNotFound(CustomTaskHasNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomTaskDoesntBelongToUserException.class)
    public ResponseEntity<AppError> handleCustomTaskDoesntBelongToUser(CustomTaskDoesntBelongToUserException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomErrorJsonParseException.class)
    public ResponseEntity<AppError> handleCustomErrorJsonParse(CustomErrorJsonParseException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<AppError> handleDateTimeParse(DateTimeParseException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
