package com.example.finalproject.manager;

import com.example.finalproject.dto.TaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.exception.*;
import com.example.finalproject.service.TaskService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.util.TaskStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserTasksManager {
    private final TaskService taskService;
    private final UserService userService;

    public List<Task> getAllTasks(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            List<Task> allTasksByUserId = taskService.getAllTasksByUserId(user.get().getId());
            log.info("Successfully gotten task list for user with id = " + user.get().getId());
            return allTasksByUserId;
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
    }

    public Task addTask(@Valid TaskCreationDto taskDto, Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        Task newTask = new Task();
        newTask.setDescription(taskDto.getDescription());
        newTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        try {
            newTask.setUser(user.get());
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
        Task savedTask;
        try {
            savedTask = taskService.saveTask(newTask);
        } catch (DataIntegrityViolationException e) {
            throw new CustomErrorJsonParseException("Error converting JSON format");
        }
        log.info("Task with id = " + newTask.getId() + "has successfully created");
        return savedTask;
    }

    public Task updateTaskStatus(Long taskId, TaskStatusUpdateDto statusDTO, Principal principal) {
        Task existingTask = taskService.getTaskById(taskId);
        if (existingTask == null) {
            throw new CustomTaskHasNotFoundException("Task with id = " + taskId + " has not found");
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            if (Objects.equals(user.get().getId(), existingTask.getUser().getId())) {
                existingTask.setTaskStatus(statusDTO.getTaskStatus());
                Task updatedTask;
                try {
                    updatedTask = taskService.saveTask(existingTask);
                } catch (DataIntegrityViolationException e) {
                    throw new CustomErrorJsonParseException("Error converting JSON format");
                }
                log.info("Task with id = " + taskId + " has successfully updated");
                return updatedTask;
            } else {
                throw new CustomTaskDoesntBelongToUserException("Task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
            }
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
    }

    public void deleteTask(Long taskId, Principal principal) {
        Task existingTask = taskService.getTaskById(taskId);
        if (existingTask == null) {
            throw new CustomTaskHasNotFoundException("Task with id = " + taskId + " has not found");
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            if (Objects.equals(user.get().getId(), existingTask.getUser().getId())) {
                taskService.deleteTaskById(taskId);
                log.info("Task with id" + taskId + " has successfully deleted");
            } else {
                throw new CustomTaskDoesntBelongToUserException("Task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
            }
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
    }
}
