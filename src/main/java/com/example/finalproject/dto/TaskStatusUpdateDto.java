package com.example.finalproject.dto;

import com.example.finalproject.util.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStatusUpdateDto {
    @Schema(description = "Status of completing of the task", example = "COMPLETED")
    private TaskStatus taskStatus;
}

