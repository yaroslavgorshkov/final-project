package com.example.finalproject.controllers;

import com.example.finalproject.dto.JwtRequest;
import com.example.finalproject.dto.JwtResponse;
import com.example.finalproject.exceptions.AppError;
import com.example.finalproject.security.CustomUserDetailsService;
import com.example.finalproject.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CustomUserDetailsService userService;

    private final JwtTokenUtils jwtTokenUtils;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "unauth!"), HttpStatus.UNAUTHORIZED);
        }
        User user =  userService.loadUserByUsername(request.getUsername());

        String token = jwtTokenUtils.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }

}
