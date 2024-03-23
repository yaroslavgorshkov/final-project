package com.example.finalproject.manager;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.User;
import com.example.finalproject.exception.AppError;
import com.example.finalproject.exception.CustomErrorJsonParseException;
import com.example.finalproject.exception.CustomUserHasNotFoundException;
import com.example.finalproject.service.ProTaskService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.util.TaskStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProTasksManager {
    private final ProTaskService proTaskService;
    private final UserService userService;

    public List<ProTask> getAllProTasks(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            List<ProTask> allProTasksByUserId = proTaskService.getAllTasksByUserId(user.get().getId());
            log.info("Successfully gotten Pro task list for user with id = " + user.get().getId());
            return allProTasksByUserId;
        } catch (Exception e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
    }

    public ProTask addProTask(@Valid ProTaskCreationDto proTaskDto, Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        ProTask newProTask = new ProTask();
        newProTask.setDescription(proTaskDto.getDescription());
        newProTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        newProTask.setDeadline(proTaskDto.getDeadline());
        newProTask.setCreationTime(LocalDateTime.now());
        try {
            newProTask.setUser(user.get());
        } catch (Exception e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
        ProTask savedProTask;
        try {
            savedProTask = proTaskService.saveProTask(newProTask);
        } catch (Exception e) {
            throw new CustomErrorJsonParseException("Error converting JSON format");
        }
        log.info("Pro task with id = " + newProTask.getId() + "has successfully created");
        return savedProTask;
    }

    public ResponseEntity<?> updateProTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatusUpdateDto statusDTO, Principal principal) {
        ProTask existingProTask = proTaskService.getProTaskById(taskId);
        if (existingProTask == null) {
            log.warn("Pro task with id = " + taskId + " has not found");
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Pro task with id = " + taskId + " has not found"), HttpStatus.NOT_FOUND);
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            if (Objects.equals(user.get().getId(), existingProTask.getUser().getId())) {
                existingProTask.setTaskStatus(statusDTO.getTaskStatus());
                ProTask updatedProTask;
                try {
                    updatedProTask = proTaskService.saveProTask(existingProTask);
                } catch (Exception e) {
                    log.warn("Error converting JSON format", e);
                    return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
                }
                log.info("Pro task with id = " + taskId + " has successfully updated");
                return ResponseEntity.ok(updatedProTask);
            } else {
                log.warn("Pro task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Pro task with id = " + taskId + " does not belong to user with id = " + user.get().getId()), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteProTask(@PathVariable Long taskId, Principal principal) {
        ProTask existingProTask = proTaskService.getProTaskById(taskId);
        if (existingProTask == null) {
            log.warn("Pro task with id = " + taskId + " has not found");
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Pro task with id = " + taskId + " has not found"), HttpStatus.NOT_FOUND);
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            if (Objects.equals(user.get().getId(), existingProTask.getUser().getId())) {
                proTaskService.deleteProTaskById(taskId);
                log.info("Pro task with id" + taskId + " has successfully deleted");
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Pro task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Pro task with id = " + taskId + " does not belong to user with id = " + user.get().getId()), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getTasksSortedByTime(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        List<ProTask> sortedTasks;
        try {
            sortedTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                    .stream()
                    .sorted(Comparator.comparing(ProTask::getCreationTime))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(sortedTasks);
    }

    public ResponseEntity<?> getTasksSortedByDeadline(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        List<ProTask> inProgressTasks;
        try {
            inProgressTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                    .stream()
                    .filter(task -> task.getTaskStatus() == TaskStatus.IN_PROGRESS)
                    .sorted(Comparator.comparing(ProTask::getDeadline))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(inProgressTasks);
    }

    public ResponseEntity<?> getExpiredTasks(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        List<ProTask> expiredTasks;
        try {
            expiredTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                    .stream()
                    .filter(task -> task.getTaskStatus() == TaskStatus.EXPIRED)
                    .toList();
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(expiredTasks);
    }
}
