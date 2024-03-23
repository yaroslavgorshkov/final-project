/*
package com.example.finalproject.controller;

import com.example.finalproject.dto.JwtRequest;
import com.example.finalproject.dto.JwtResponse;
import com.example.finalproject.manager.CustomAuthManager;
import com.example.finalproject.util.JwtTokenUtils;
import com.example.finalproject.util.ObjectMapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private CustomAuthManager customAuthManager;

    @Test
    void testCreateAuthToken_ValidRequest_ReturnsToken() throws Exception {
        JwtRequest request = new JwtRequest("username", "password");
        String token = "generated_token";
        doReturn(ResponseEntity.ok(new JwtResponse(token))).when(customAuthManager).createAuthToken(any());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void AuthControllerTest_createAuthToken_Unauthorized() throws Exception {
        JwtRequest request = new JwtRequest("invalid_username", "invalid_password");
        when(customAuthManager.createAuthToken(request)).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(request)))
                .andExpect(status().isUnauthorized());
    }
}
*/
