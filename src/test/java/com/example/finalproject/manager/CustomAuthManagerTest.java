package com.example.finalproject.manager;

import com.example.finalproject.dto.JwtRequest;
import com.example.finalproject.dto.JwtResponse;
import com.example.finalproject.security.CustomUserDetailsService;
import com.example.finalproject.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebMvcTest(CustomAuthManager.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CustomAuthManagerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomAuthManager customAuthManager;

    @Test
    void CustomAuthManagerTest_createAuthToken_success() {
        JwtRequest request = new JwtRequest("username", "password");

        List<SimpleGrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        User user = new User("username", "password", roles);

        String expectedToken = "token";

        when(userService.loadUserByUsername("username")).thenReturn(user);
        when(jwtTokenUtils.generateToken(user)).thenReturn(expectedToken);

        JwtResponse jwtResponse = customAuthManager.createAuthToken(request);

        assertEquals(expectedToken, jwtResponse.getToken());
    }
}
