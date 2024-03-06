package com.example.finalproject.controllers;

import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.services.ProTaskService;
import com.example.finalproject.services.TaskService;
import com.example.finalproject.services.UserService;
import com.example.finalproject.util.TaskStatus;
import com.example.finalproject.util.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ProTaskService proTaskService;
    private final TaskService taskService;
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateTask(@PathVariable Long userId, @RequestBody User user) {
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        user.setId(userId);
        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User newUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    @GetMapping("/statistics")
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
                + allProTasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.EXPIRED).count();;

        String statistics = "Amount of users: " + totalUsers + "\n" +
                "with PRO status: " + proUsers + "\n" +
                "with ADMIN status: " + adminUsers + "\n" +
                "Amount of tasks: " + totalTasks + "\n" +
                "COMPLETED: " + completedTasks + "\n" +
                "IN PROGRESS: " + inProgressTasks + "\n" +
                "EXPIRED: " + expiredTasks + "\n";

        return statistics;
    }
}
