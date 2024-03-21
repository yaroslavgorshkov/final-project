package com.example.finalproject.entity;

import com.example.finalproject.util.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id of the task")
    private Long id;

    @Schema(description = "Description of the task")
    private String description;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status of completing of the the task", example = "IN_PROGRESS")
    private TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @Schema(description = "User id")
    private User user;
}
