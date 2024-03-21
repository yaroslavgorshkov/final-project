package com.example.finalproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProTaskCreationDto {
    @Schema(description = "Task description", example = "Do homework", minLength = 3, maxLength = 200)
    @NotBlank(message = "Description mush not be blank")
    @Size(min = 3, max = 200, message = "Description mush be less than 200 and bigger than 3 characters")
    private String description;

    @Schema(description = "Task deadline", example = "2024-03-26 12:21:37.535275")
    @NotBlank(message = "Deadline mush not be blank")
    private LocalDateTime deadline;
}
