package com.example.finalproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProTaskCreationDto {
    @NotBlank(message = "Description mush not be blank")
    @Size(min = 3, max = 300, message = "Description mush be less than 300 and bigger than 3")
    private String description;

    @NotBlank(message = "Deadline mush not be blank")
    private LocalDateTime deadline;
}
