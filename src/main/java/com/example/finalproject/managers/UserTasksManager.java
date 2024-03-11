package com.example.finalproject.managers;

import com.example.finalproject.dto.TaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.services.TaskService;
import com.example.finalproject.services.UserService;
import com.example.finalproject.util.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserTasksManager {
    private final TaskService taskService;
    private final UserService userService;

    public ResponseEntity<List<Task>> getAllTasks(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            List<Task> allTasksByUserId = taskService.getAllTasksByUserId(user.get().getId());
            log.info("Successfully gotten task list for user with id = " + user.get().getId());
            return ResponseEntity.ok(allTasksByUserId);
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Task> addTask(TaskCreationDto taskDto, Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        Task newTask = new Task();
        newTask.setDescription(taskDto.getDescription());
        newTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        try {
            newTask.setUser(user.get());
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return ResponseEntity.badRequest().build();
        }
        Task savedTask;
        try {
            savedTask = taskService.saveTask(newTask);
        } catch (Exception e) {
            log.warn("Error converting JSON format!", e);
            return ResponseEntity.badRequest().build();
        }
        log.info("Task with id = " + newTask.getId() + "has successfully created");
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    public ResponseEntity<Task> updateTaskStatus(Long taskId, TaskStatusUpdateDto statusDTO, Principal principal) {
        Task existingTask = taskService.getTaskById(taskId);
        if (existingTask == null) {
            log.warn("Task with id = " + taskId + " has not found");
            return ResponseEntity.notFound().build();
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            if (Objects.equals(user.get().getId(), existingTask.getUser().getId())) {
                existingTask.setTaskStatus(statusDTO.getTaskStatus());
                Task updatedTask;
                try {
                    updatedTask = taskService.saveTask(existingTask);
                } catch (Exception e) {
                    log.warn("Error converting JSON format", e);
                    return ResponseEntity.badRequest().build();
                }
                log.info("Task with id = " + taskId + " has successfully updated");
                return ResponseEntity.ok(updatedTask);
            } else {
                log.warn("Task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Void> deleteTask(Long taskId, Principal principal) {
        Task existingTask = taskService.getTaskById(taskId);
        if (existingTask == null) {
            log.warn("Task with id = " + taskId + " has not found");
            return ResponseEntity.notFound().build();
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            if (Objects.equals(user.get().getId(), existingTask.getUser().getId())) {
                taskService.deleteTaskById(taskId);
                log.info("Task with id" + taskId + " has successfully deleted");
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
