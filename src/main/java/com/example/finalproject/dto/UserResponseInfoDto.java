package com.example.finalproject.dto;

import com.example.finalproject.util.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseInfoDto {
    @Schema(description = "Id of the user")
    private Long id;

    @Schema(description = "Username of the user")
    private String username;

    @Schema(description = "Role of the user", example = "PRO")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}
