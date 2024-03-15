package com.example.finalproject.controllers;

import com.example.finalproject.entity.User;
import com.example.finalproject.managers.UsersManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UsersManager usersManager;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return usersManager.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        return usersManager.deleteUser(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody @Valid User user) {
        return usersManager.updateUser(userId, user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        return usersManager.addUser(user);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getUsersStatistics() {
        return usersManager.getUsersStatistics();
    }
}
