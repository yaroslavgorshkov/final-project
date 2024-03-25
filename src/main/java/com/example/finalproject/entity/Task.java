package com.example.finalproject.entity;

import com.example.finalproject.util.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "user_tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task{
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(description, task.description) && taskStatus == task.taskStatus && Objects.equals(user, task.user);
    }
}
