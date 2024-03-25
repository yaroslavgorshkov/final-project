package com.example.finalproject.manager;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.User;
import com.example.finalproject.exception.*;
import com.example.finalproject.service.ProTaskService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.util.TaskStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        } catch (NoSuchElementException e) {
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
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
        ProTask savedProTask;
        try {
            savedProTask = proTaskService.saveProTask(newProTask);
        } catch (DataIntegrityViolationException e) {
            throw new CustomErrorJsonParseException("Error converting JSON format");
        }
        log.info("Pro task with id = " + newProTask.getId() + "has successfully created");
        return savedProTask;
    }

    public ProTask updateProTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatusUpdateDto statusDTO, Principal principal) {
        ProTask existingProTask = proTaskService.getProTaskById(taskId);
        if (existingProTask == null) {
            throw new CustomTaskHasNotFoundException("Pro task with id = " + taskId + " has not found");
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            if (Objects.equals(user.get().getId(), existingProTask.getUser().getId())) {
                existingProTask.setTaskStatus(statusDTO.getTaskStatus());
                ProTask updatedProTask;
                try {
                    updatedProTask = proTaskService.saveProTask(existingProTask);
                } catch (DataIntegrityViolationException e) {
                    throw new CustomErrorJsonParseException("Error converting JSON format");
                }
                log.info("Pro task with id = " + taskId + " has successfully updated");
                return updatedProTask;
            } else {
                throw new CustomTaskDoesntBelongToUserException("Pro task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
            }
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
    }

    public void deleteProTask(@PathVariable Long taskId, Principal principal) {
        ProTask existingProTask = proTaskService.getProTaskById(taskId);
        if (existingProTask == null) {
            throw new CustomTaskHasNotFoundException("Pro task with id = " + taskId + " has not found");
        }
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        try {
            if (Objects.equals(user.get().getId(), existingProTask.getUser().getId())) {
                proTaskService.deleteProTaskById(taskId);
                log.info("Pro task with id" + taskId + " has successfully deleted");
            } else {
                throw new CustomTaskDoesntBelongToUserException("Pro task with id = " + taskId + " does not belong to user with id = " + user.get().getId());
            }
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
    }

    public List<ProTask> getTasksSortedByTime(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        List<ProTask> sortedTasks;
        try {
            sortedTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                    .stream()
                    .sorted(Comparator.comparing(ProTask::getCreationTime))
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
        return sortedTasks;
    }

    public List<ProTask> getTasksSortedByDeadline(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        List<ProTask> inProgressTasks;
        try {
            inProgressTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                    .stream()
                    .filter(task -> task.getTaskStatus() == TaskStatus.IN_PROGRESS)
                    .sorted(Comparator.comparing(ProTask::getDeadline))
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
        return inProgressTasks;
    }

    public List<ProTask> getExpiredTasks(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        List<ProTask> expiredTasks;
        try {
            expiredTasks = proTaskService.getAllTasksByUserId(user.get().getId())
                    .stream()
                    .filter(task -> task.getTaskStatus() == TaskStatus.EXPIRED)
                    .toList();
        } catch (NoSuchElementException e) {
            throw new CustomUserHasNotFoundException("User has not found");
        }
        return expiredTasks;
    }
}
