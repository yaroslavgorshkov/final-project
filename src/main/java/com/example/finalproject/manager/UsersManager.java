package com.example.finalproject.manager;

import com.example.finalproject.dto.UserResponseInfoDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.exception.AppError;
import com.example.finalproject.service.ProTaskService;
import com.example.finalproject.service.TaskService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.util.TaskStatus;
import com.example.finalproject.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersManager {
    private final UserService userService;
    private final ProTaskService proTaskService;
    private final TaskService taskService;

    public ResponseEntity<List<UserResponseInfoDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseInfoDto> userResponseInfoDtoList =
                usersToUserResponseInfoDtoConverter(users);
        log.info("Successfully gotten task list for admin");
        return ResponseEntity.ok(userResponseInfoDtoList);
    }

    private List<UserResponseInfoDto> usersToUserResponseInfoDtoConverter(List<User> users) {
        return users.stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
    }

    private UserResponseInfoDto convertUserToDto(User user) {
        return new UserResponseInfoDto(user.getId(), user.getUsername(), user.getUserRole());
    }

    public ResponseEntity<?> deleteUser(Long userId) {
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            log.warn("User with id = " + userId + " has not found");
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "User with id = " + userId + " has not found"), HttpStatus.NOT_FOUND);
        }
        userService.deleteUserById(userId);
        log.info("Successfully deleted user with id = " + userId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> updateUser(Long userId, User user) {
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            log.warn("User with id = " + userId + " has not found");
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "User with id = " + userId + " has not found"), HttpStatus.NOT_FOUND);
        }
        user.setId(userId);
        User updatedUser = userService.saveUser(user);
        log.info("Successfully updated user with id = " + userId);
        return ResponseEntity.ok(updatedUser);
    }

    public ResponseEntity<User> addUser(User user) {
        User newUser;
        newUser = userService.saveUser(user);
        log.info("Successfully saved user with id = " + newUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    public String getUsersStatistics() {
        List<User> allUsers = userService.getAllUsers();

        int totalUsers = allUsers.size();
        long proUsers = allUsers.stream().filter(user -> user.getUserRole() == UserRole.PRO).count();
        long adminUsers = allUsers.stream().filter(user -> user.getUserRole() == UserRole.ADMIN).count();

        List<ProTask> allProTasks = proTaskService.getAllProTasks();
        List<Task> allTasks = taskService.getAllTasks();

        int totalTasks = allTasks.size() + allProTasks.size();

        long completedTasks = allTasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.COMPLETED).count()
                + allProTasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.COMPLETED).count();
        long inProgressTasks = allTasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.IN_PROGRESS).count()
                + allProTasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.IN_PROGRESS).count();
        long expiredTasks = allTasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.EXPIRED).count()
                + allProTasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.EXPIRED).count();
        ;

        String statistics = "Amount of users: " + totalUsers + "\n" +
                "with PRO status: " + proUsers + "\n" +
                "with ADMIN status: " + adminUsers + "\n" +
                "Amount of tasks: " + totalTasks + "\n" +
                "COMPLETED: " + completedTasks + "\n" +
                "IN PROGRESS: " + inProgressTasks + "\n" +
                "EXPIRED: " + expiredTasks + "\n";
        log.info("Getting users statistics");
        return statistics;
    }
}
