package com.example.finalproject.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class HelloController {

    @GetMapping
    public String sayHello() {
        return "Hello everyone! Welcome to our Mega Tasks Manager!";
    }
}
