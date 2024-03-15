package com.example.finalproject.controllers;

import com.example.finalproject.dto.JwtRequest;
import com.example.finalproject.managers.CustomAuthManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return customAuthManager.createAuthToken(request);
    }
}
