package com.example.finalproject.dto;

import com.example.finalproject.util.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProTaskCreationDto {
    private String description;
    private LocalDateTime deadline;
}
