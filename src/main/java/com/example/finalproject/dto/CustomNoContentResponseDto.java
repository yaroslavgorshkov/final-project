package com.example.finalproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomNoContentResponseDto {
    @Schema(description = "Message for informing admin in successfully deleting of user")
    private String message;
}
