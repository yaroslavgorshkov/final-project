package com.example.finalproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {
    @Schema(description = "Username of the user", minLength = 5, maxLength = 20)
    @NotBlank(message = "Username field must not be blank")
    @Size(min = 5, max = 20, message = "Username field must be between 5 and 20 characters")
    private String username;

    @Schema(description = "Password of the user", minLength = 5, maxLength = 100)
    @NotBlank(message = "Password field must not be blank")
    @Size(min = 5, max = 100, message = "Password field must be between 5 and 100 characters")
    private String password;
}

