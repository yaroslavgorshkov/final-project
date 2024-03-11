package com.example.finalproject.controllers;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.managers.ProTasksManager;
import com.example.finalproject.managers.UserTasksManager;
import com.example.finalproject.util.TaskCreationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/v1/api/tasks")
@RequiredArgsConstructor
@Validated
public class UserTasksController {
    private final ProTasksManager proTasksManager;
    private final UserTasksManager userTasksManager;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> getAllTasks(Principal principal) {
        boolean isPro = hasRole(principal);
        if(isPro) {
            return proTasksManager.getAllProTasks(principal);
        }
        return userTasksManager.getAllTasks(principal);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> addTask(@RequestBody @Valid Object taskDto, Principal principal) {
        boolean isPro = hasRole(principal);
        if(isPro) {
            ProTaskCreationDto proTaskCreationDto = TaskCreationUtils.getProTaskCreationDto(taskDto);
            return proTasksManager.addProTask(proTaskCreationDto, principal);
        } else {
            TaskCreationDto taskCreationDto = TaskCreationUtils.getTaskCreationDto(taskDto);
            return userTasksManager.addTask(taskCreationDto, principal);
        }
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestBody @Valid TaskStatusUpdateDto statusDTO, Principal principal) {
        boolean isPro = hasRole(principal);
        if(isPro) {
            return proTasksManager.updateProTaskStatus(taskId, statusDTO, principal);
        }
        return userTasksManager.updateTaskStatus(taskId, statusDTO, principal);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Principal principal) {
        boolean isPro = hasRole(principal);
        if(isPro) {
            return proTasksManager.deleteProTask(taskId, principal);
        }
        return userTasksManager.deleteTask(taskId, principal);
    }

    @GetMapping("/by-time")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<List<ProTask>> getTasksSortedByTime(Principal principal) {
        return proTasksManager.getTasksSortedByTime(principal);
    }

    @GetMapping("/deadline-soon")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<List<ProTask>> getTasksSortedByDeadline(Principal principal) {
        return proTasksManager.getTasksSortedByDeadline(principal);
    }

    @GetMapping("/expired")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<List<ProTask>> getExpiredTasks(Principal principal) {
        return proTasksManager.getExpiredTasks(principal);
    }

    private boolean hasRole(Principal principal) {
        Authentication authentication = (Authentication) principal;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getAuthority().contains("PRO"));
    }
}
