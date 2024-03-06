package com.example.finalproject.controllers;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.User;
import com.example.finalproject.services.ProTaskService;
import com.example.finalproject.services.UserService;
import com.example.finalproject.util.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pro/tasks")
@RequiredArgsConstructor
public class ProTasksController {
    private final ProTaskService proTaskService;
    private final UserService userService;
    @GetMapping
    public ResponseEntity<?> getAllTasks(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        return ResponseEntity.ok(proTaskService.getAllTasksByUserId(user.get().getId()));
    }

    @PostMapping
    public ResponseEntity<ProTask> addTask(@RequestBody ProTaskCreationDto proTaskDto, Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        ProTask newProTask = new ProTask();
        newProTask.setDescription(proTaskDto.getDescription());
        newProTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        newProTask.setDeadline(proTaskDto.getDeadline());
        newProTask.setCreationTime(LocalDateTime.now());
        newProTask.setUser(user.get());

        ProTask savedProTask = proTaskService.saveProTask(newProTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProTask);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ProTask> updateTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatusUpdateDto statusDTO, Principal principal) {
        ProTask existingProTask = proTaskService.getProTaskById(taskId);
        if (existingProTask == null) {
            return ResponseEntity.notFound().build();
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        if(Objects.equals(user.get().getId(), existingProTask.getUser().getId()))  {
            existingProTask.setTaskStatus(statusDTO.getTaskStatus());
            ProTask updatedProTask = proTaskService.saveProTask(existingProTask);
            return ResponseEntity.ok(updatedProTask);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Principal principal) {
        ProTask existingProTask = proTaskService.getProTaskById(taskId);
        if (existingProTask == null) {
            return ResponseEntity.notFound().build();
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        if(Objects.equals(user.get().getId(), existingProTask.getUser().getId()))  {
            proTaskService.deleteProTaskById(taskId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/by-time")
    public ResponseEntity<List<ProTask>> getTasksSortedByTime(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        List<ProTask> sortedTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                .stream()
                .sorted(Comparator.comparing(ProTask::getCreationTime))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedTasks);
    }

    @GetMapping("/deadline-soon")
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

    @GetMapping("/expired")
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
