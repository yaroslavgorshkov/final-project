package com.example.finalproject.managers;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.services.ProTaskService;
import com.example.finalproject.services.TaskService;
import com.example.finalproject.services.UserService;
import com.example.finalproject.util.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProTasksManager {
    private final ProTaskService proTaskService;
    private final UserService userService;

    public ResponseEntity<List<ProTask>> getAllProTasks(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            List<ProTask> allProTasksByUserId = proTaskService.getAllTasksByUserId(user.get().getId());
            log.info("Successfully gotten Pro task list for user with id = " + user.get().getId());
            return ResponseEntity.ok(allProTasksByUserId);
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<ProTask> addProTask(@RequestBody ProTaskCreationDto proTaskDto, Principal principal) {
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
            log.warn("User has not found: {}", username, e);
            return ResponseEntity.badRequest().build();
        }
        ProTask savedProTask;
        try {
            savedProTask = proTaskService.saveProTask(newProTask);
        } catch (Exception e) {
            log.warn("Error converting JSON format", e);
            return ResponseEntity.badRequest().build();
        }
        log.info("Pro task with id = " + newProTask.getId() + "has successfully created");
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProTask);
    }

    public ResponseEntity<ProTask> updateProTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatusUpdateDto statusDTO, Principal principal) {
        ProTask existingProTask = proTaskService.getProTaskById(taskId);
        if (existingProTask == null) {
            log.warn("Pro task with id = " + taskId + " has not found");
            return ResponseEntity.notFound().build();
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
                    return ResponseEntity.badRequest().build();
                }
                log.info("Pro task with id = " + taskId + " has successfully updated");
                return ResponseEntity.ok(updatedProTask);
            } else {
                log.warn("Pro task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Void> deleteProTask(@PathVariable Long taskId, Principal principal) {
        ProTask existingProTask = proTaskService.getProTaskById(taskId);
        if (existingProTask == null) {
            log.warn("Pro task with id = " + taskId + " has not found");
            return ResponseEntity.notFound().build();
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
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.warn("User has not found: {}", username, e);
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<List<ProTask>> getTasksSortedByTime(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        List<ProTask> sortedTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                .stream()
                .sorted(Comparator.comparing(ProTask::getCreationTime))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedTasks);
    }

    public ResponseEntity<List<ProTask>> getTasksSortedByDeadline(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        List<ProTask> inProgressTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                .stream()
                .filter(task -> task.getTaskStatus() == TaskStatus.IN_PROGRESS)
                .sorted(Comparator.comparing(ProTask::getDeadline))
                .collect(Collectors.toList());

        return ResponseEntity.ok(inProgressTasks);
    }

    public ResponseEntity<List<ProTask>> getExpiredTasks(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        List<ProTask> expiredTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                .stream()
                .filter(task -> task.getTaskStatus() == TaskStatus.EXPIRED)
                .toList();

        return ResponseEntity.ok(expiredTasks);
    }
}
