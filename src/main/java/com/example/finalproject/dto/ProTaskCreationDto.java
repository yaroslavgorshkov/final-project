package com.example.finalproject.dto;

import com.example.finalproject.util.CustomLocalDateTimeUtils.LocalDateTimeDeserializer;
import com.example.finalproject.util.CustomLocalDateTimeUtils.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Schema(description = "Task deadline", example = "2024-03-10T12:00:00")
    @NotNull(message = "Deadline must not be null")
    @Future(message = "Deadline must be in the future")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime deadline;
}
