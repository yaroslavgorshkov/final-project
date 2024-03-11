package com.example.finalproject.dto;

import com.example.finalproject.util.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseInfoDto {
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}
