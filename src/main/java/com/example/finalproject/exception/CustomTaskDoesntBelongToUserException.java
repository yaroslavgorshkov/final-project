package com.example.finalproject.exception;

public class CustomTaskDoesntBelongToUserException extends RuntimeException{
    public CustomTaskDoesntBelongToUserException(String message) {
        super(message);
    }
}