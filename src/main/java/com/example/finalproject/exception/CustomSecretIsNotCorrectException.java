package com.example.finalproject.exception;

public class CustomSecretIsNotCorrectException extends RuntimeException {
    public CustomSecretIsNotCorrectException(String message) {
        super(message);
    }
}
