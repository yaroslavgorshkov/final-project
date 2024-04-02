package com.example.finalproject.exception;

public class CustomJwtExpiredException extends RuntimeException{
    public CustomJwtExpiredException(String message) {
        super(message);
    }
}
