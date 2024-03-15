package com.example.finalproject.entity;

import com.example.finalproject.util.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "pro_tasks")
@Getter
@Setter
public class ProTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Description of the task")
    private String description;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status of completing of the task", example = "COMPLETED")
    private TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @Schema(description = "User id")
    private User user;

    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH")
    @Schema(description = "Time of the task creation", example = "2024-03-15 11:21:37.535275")
    private LocalDateTime creationTime;

    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH")
    @Schema(description = "Task deadline", example = "2024-03-26 12:21:37.535275")
    private LocalDateTime deadline;
}
