package com.example.finalproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {
    @NotBlank(message = "Username field mush not be blank")
    @NotNull(message = "Username field mush not be null")
    private String username;
    @NotBlank(message = "Password field mush not be blank")
    @NotNull(message = "Password field mush not be null")
    private String password;
}
