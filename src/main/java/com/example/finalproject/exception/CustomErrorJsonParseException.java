package com.example.finalproject.exception;

public class CustomErrorJsonParseException extends RuntimeException{
    public CustomErrorJsonParseException(String message) {
        super(message);
    }
}
