package com.example.finalproject.controller;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.manager.ProTasksManager;
import com.example.finalproject.manager.UserTasksManager;
import com.example.finalproject.util.RoleIdentifier;
import com.example.finalproject.util.TaskCreationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/api/tasks")
@RequiredArgsConstructor
public class UserTasksController {
    private final ProTasksManager proTasksManager;
    private final UserTasksManager userTasksManager;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> getAllTasks(Principal principal) {
        boolean isPro = RoleIdentifier.isPro(principal);
        if(isPro) {
            return new ResponseEntity<>(proTasksManager.getAllProTasks(principal), HttpStatus.OK);
        }
        return new ResponseEntity<>(userTasksManager.getAllTasks(principal), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> addTask(@RequestBody Object taskDto, Principal principal) {
        boolean isPro = RoleIdentifier.isPro(principal);
        if(isPro) {
            ProTaskCreationDto proTaskCreationDto = TaskCreationUtils.getProTaskCreationDto(taskDto);
            return new ResponseEntity<>(proTasksManager.addProTask(proTaskCreationDto, principal), HttpStatus.CREATED);

        } else {
            TaskCreationDto taskCreationDto = TaskCreationUtils.getTaskCreationDto(taskDto);
            return new ResponseEntity<>(userTasksManager.addTask(taskCreationDto, principal), HttpStatus.CREATED);
        }
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatusUpdateDto statusDTO, Principal principal) {
        boolean isPro = RoleIdentifier.isPro(principal);
        if(isPro) {
            return proTasksManager.updateProTaskStatus(taskId, statusDTO, principal);
        }
        return userTasksManager.updateTaskStatus(taskId, statusDTO, principal);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, Principal principal) {
        boolean isPro = RoleIdentifier.isPro(principal);
        if(isPro) {
            return proTasksManager.deleteProTask(taskId, principal);
        }
        return userTasksManager.deleteTask(taskId, principal);
    }

    @GetMapping("/by-time")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<?> getTasksSortedByTime(Principal principal) {
        return proTasksManager.getTasksSortedByTime(principal);
    }

    @GetMapping("/deadline-soon")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<?> getTasksSortedByDeadline(Principal principal) {
        return proTasksManager.getTasksSortedByDeadline(principal);
    }

    @GetMapping("/expired")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<?> getExpiredTasks(Principal principal) {
        return proTasksManager.getExpiredTasks(principal);
    }
}
