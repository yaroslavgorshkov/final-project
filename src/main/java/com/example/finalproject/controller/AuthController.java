package com.example.finalproject.controller;

import com.example.finalproject.dto.JwtRequest;
import com.example.finalproject.manager.CustomAuthManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CustomAuthManager customAuthManager;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody @Valid JwtRequest request) {
        return new ResponseEntity<>(customAuthManager.createAuthToken(request), HttpStatus.OK);
    }
}
