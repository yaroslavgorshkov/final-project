package com.example.finalproject.dto;

import com.example.finalproject.util.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusUpdateDto {
    @Schema(description = "Status of completing of the task", example = "COMPLETED")
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
}

