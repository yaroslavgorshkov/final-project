package com.example.finalproject.util;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskCreationDto;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class TaskCreationUtils {
    public static TaskCreationDto getTaskCreationDto(Object taskDto) {
        TaskCreationDto taskCreationDto = new TaskCreationDto();
        taskCreationDto.setDescription((String) ((LinkedHashMap<?, ?>) taskDto).get("description"));
        return taskCreationDto;
    }

    public static ProTaskCreationDto getProTaskCreationDto(Object taskDto) {
        ProTaskCreationDto proTaskCreationDto = new ProTaskCreationDto();
        proTaskCreationDto.setDescription((String) ((LinkedHashMap<?, ?>) taskDto).get("description"));
        proTaskCreationDto.setDeadline(LocalDateTime.parse((String) ((LinkedHashMap<?, ?>) taskDto).get("deadline")));
        return proTaskCreationDto;
    }
}
