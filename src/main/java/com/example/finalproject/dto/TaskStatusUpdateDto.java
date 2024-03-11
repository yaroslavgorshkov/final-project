package com.example.finalproject.dto;

import com.example.finalproject.util.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskStatusUpdateDto {
    @NotBlank(message = "Task status must not be blank")
    private TaskStatus taskStatus;
}

