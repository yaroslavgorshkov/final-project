package com.example.finalproject.controller;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.manager.ProTasksManager;
import com.example.finalproject.manager.UserTasksManager;
import com.example.finalproject.util.RoleIdentifier;
import com.example.finalproject.util.TaskCreationUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1/api/tasks")
@RequiredArgsConstructor
public class UserTasksController {
    private final ProTasksManager proTasksManager;
    private final UserTasksManager userTasksManager;
    private final Validator validator;

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
    public ResponseEntity<?> addTask(@RequestBody @Valid Object taskDto, Principal principal) {
        boolean isPro = RoleIdentifier.isPro(principal);
        if(isPro) {
            ProTaskCreationDto proTaskCreationDto = TaskCreationUtils.getProTaskCreationDto(taskDto);
            validate(proTaskCreationDto);
            return new ResponseEntity<>(proTasksManager.addProTask(proTaskCreationDto, principal), HttpStatus.CREATED);
        } else {
            TaskCreationDto taskCreationDto = TaskCreationUtils.getTaskCreationDto(taskDto);
            validate(taskCreationDto);
            return new ResponseEntity<>(userTasksManager.addTask(taskCreationDto, principal), HttpStatus.CREATED);
        }
    }

    private void validate(Object dto) {
        Set<ConstraintViolation<Object>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatusUpdateDto statusDTO, Principal principal) {
        boolean isPro = RoleIdentifier.isPro(principal);
        if(isPro) {
            return new ResponseEntity<>(proTasksManager.updateProTaskStatus(taskId, statusDTO, principal), HttpStatus.OK);
        }
        return new ResponseEntity<>(userTasksManager.updateTaskStatus(taskId, statusDTO, principal), HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('PRO', 'USER')")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, Principal principal) {
        boolean isPro = RoleIdentifier.isPro(principal);
        if(isPro) {
            proTasksManager.deleteProTask(taskId, principal);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        userTasksManager.deleteTask(taskId, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-time")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<?> getTasksSortedByTime(Principal principal) {
        return new ResponseEntity<>(proTasksManager.getTasksSortedByTime(principal), HttpStatus.OK);
    }

    @GetMapping("/deadline-soon")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<?> getTasksSortedByDeadline(Principal principal) {
        return new ResponseEntity<>(proTasksManager.getTasksSortedByDeadline(principal), HttpStatus.OK);
    }

    @GetMapping("/expired")
    @PreAuthorize("hasAuthority('PRO')")
    public ResponseEntity<?> getExpiredTasks(Principal principal) {
        List<ProTask> expiredTasks = proTasksManager.getExpiredTasks(principal);
        return new ResponseEntity<>(expiredTasks, HttpStatus.OK);
    }
}
