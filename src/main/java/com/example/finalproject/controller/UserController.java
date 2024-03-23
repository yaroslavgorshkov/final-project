package com.example.finalproject.controller;

import com.example.finalproject.dto.CustomNoContentResponseDto;
import com.example.finalproject.dto.UserResponseInfoDto;
import com.example.finalproject.entity.User;
import com.example.finalproject.manager.UsersManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UsersManager usersManager;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponseInfoDto>> getAllUsers() {
        return new ResponseEntity<>(usersManager.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CustomNoContentResponseDto> deleteUser(@PathVariable Long userId) {
        usersManager.deleteUser(userId);
        String message = "Successfully deleted user with id = " + userId;
        return new ResponseEntity<>(new CustomNoContentResponseDto(message), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody @Valid User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        User updUser = usersManager.updateUser(userId, user);
        return ResponseEntity.ok()
                .headers(headers)
                .body(updUser);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        return new ResponseEntity<>(usersManager.addUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getUsersStatistics() {
        return usersManager.getUsersStatistics();
    }
}
