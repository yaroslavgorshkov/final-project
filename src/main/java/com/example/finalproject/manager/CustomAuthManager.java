package com.example.finalproject.manager;

import com.example.finalproject.dto.JwtRequest;
import com.example.finalproject.dto.JwtResponse;
import com.example.finalproject.security.CustomUserDetailsService;
import com.example.finalproject.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthManager {
    private final CustomUserDetailsService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public JwtResponse createAuthToken(JwtRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user =  userService.loadUserByUsername(request.getUsername());

        String token = jwtTokenUtils.generateToken(user);
        return new JwtResponse(token);
    }
}
