package com.example.finalproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskCreationDto {
    @NotBlank(message = "Description mush not be blank")
    @Size(min = 3, max = 300, message = "Description mush be less than 300 and bigger than 3")
    private String description;
}
