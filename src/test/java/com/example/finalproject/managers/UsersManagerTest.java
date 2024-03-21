package com.example.finalproject.managers;

import com.example.finalproject.dto.UserResponseInfoDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.services.ProTaskService;
import com.example.finalproject.services.TaskService;
import com.example.finalproject.services.UserService;
import com.example.finalproject.util.TaskStatus;
import com.example.finalproject.util.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(UsersManager.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UsersManagerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private ProTaskService proTaskService;
    @MockBean
    private TaskService taskService;

    @Autowired
    private UsersManager usersManager;
    private static final User STANDART_USER = User.builder().id(1L).username("user1").password("password1").userRole(UserRole.USER).build();
    private static final User STANDART_PRO_USER = User.builder().id(1L).username("user2").password("password2").userRole(UserRole.PRO).build();

    @Test
    void UsersManagerTest_getAllUsers_success() {
        List<User> users = List.of(
                STANDART_USER,
                STANDART_PRO_USER
        );
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserResponseInfoDto>> response = usersManager.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users.size(), Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void UsersManagerTest_deleteUser_success() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(STANDART_USER);
        /*doNothing().when(userService).deleteUserById(userId);*/

        ResponseEntity<?> response = usersManager.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void UsersManagerTest_deleteUser_UserNotFound() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(null);

        ResponseEntity<?> response = usersManager.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void UsersManagerTest_updateUser_success() {
        Long userId = 1L;
        User user = new User();
        user.setUsername("updatedUsername");
        user.setUserRole(UserRole.PRO);

        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.saveUser(user)).thenReturn(user);

        ResponseEntity<?> response = usersManager.updateUser(userId, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        User updatedUser = (User) response.getBody();
        assertEquals(user.getUsername(), updatedUser.getUsername());
        assertEquals(user.getUserRole(), updatedUser.getUserRole());
    }

    @Test
    void UsersManagerTest_updateUser_NotFound() {
        Long userId = 1L;
        User user = new User();
        user.setUsername("updatedUsername");
        user.setUserRole(UserRole.PRO);

        when(userService.getUserById(userId)).thenReturn(null);

        ResponseEntity<?> response = usersManager.updateUser(userId, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void UsersManagerTest_addUser_success() {
        User user = new User();
        user.setUsername("newUsername");
        user.setUserRole(UserRole.USER);

        when(userService.saveUser(user)).thenReturn(user);

        ResponseEntity<User> response = usersManager.addUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUsersStatistics_ValidData_Success() {
        List<ProTask> proTasks = Arrays.asList(
                new ProTask(1L, "task1", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(2L, "task2", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(3L, "task3", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000))
        );
        List<Task> tasks = Arrays.asList(
                new Task(1L, "task4", TaskStatus.COMPLETED, STANDART_USER),
                new Task(2L, "task5", TaskStatus.IN_PROGRESS, STANDART_USER),
                new Task(3L, "task6", TaskStatus.EXPIRED, STANDART_USER)
        );

        when(userService.getAllUsers()).thenReturn(users);
        when(proTaskService.getAllProTasks()).thenReturn(proTasks);
        when(taskService.getAllTasks()).thenReturn(tasks);

        String statistics = usersManager.getUsersStatistics();

        assertTrue(statistics.contains("Amount of users: 3"));
        assertTrue(statistics.contains("with PRO status: 1"));
        assertTrue(statistics.contains("with ADMIN status: 1"));
        assertTrue(statistics.contains("Amount of tasks: 6"));
        assertTrue(statistics.contains("COMPLETED: 2"));
        assertTrue(statistics.contains("IN PROGRESS: 2"));
        assertTrue(statistics.contains("EXPIRED: 2"));
    }
}