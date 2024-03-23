package com.example.finalproject.exception;

public class CustomUserHasNotFoundException extends RuntimeException{
    public CustomUserHasNotFoundException(String message) {
        super(message);
    }
}
