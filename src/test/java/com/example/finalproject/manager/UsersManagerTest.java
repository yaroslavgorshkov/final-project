package com.example.finalproject.manager;

import com.example.finalproject.dto.UserResponseInfoDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.exception.CustomUserHasNotFoundException;
import com.example.finalproject.service.ProTaskService;
import com.example.finalproject.service.TaskService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.util.JwtTokenUtils;
import com.example.finalproject.util.TaskStatus;
import com.example.finalproject.util.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UsersManager usersManager;
    private static final User STANDART_USER = User.builder().id(1L).username("user1").password("password1").userRole(UserRole.USER).build();
    private static final User STANDART_PRO_USER = User.builder().id(2L).username("user2").password("password2").userRole(UserRole.PRO).build();

    @Test
    void UsersManagerTest_getAllUsers_success() {
        List<User> users = new ArrayList<>();
        users.add(STANDART_USER);
        users.add(STANDART_PRO_USER);

        List<UserResponseInfoDto> expectedUserResponseInfoDtoList = new ArrayList<>();
        expectedUserResponseInfoDtoList.add(new UserResponseInfoDto(1L, "user1", UserRole.USER));
        expectedUserResponseInfoDtoList.add(new UserResponseInfoDto(2L, "user2", UserRole.PRO));

        when(userService.getAllUsers()).thenReturn(users);

        List<UserResponseInfoDto> actualUserResponseInfoDtoList = usersManager.getAllUsers();

        assertEquals(expectedUserResponseInfoDtoList, actualUserResponseInfoDtoList);
    }

    @Test
    void UsersManagerTest_deleteUser_success() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(STANDART_USER);
        usersManager.deleteUser(userId);
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void UsersManagerTest_deleteUser_userHasNotFound() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(null);
        assertThrows(CustomUserHasNotFoundException.class, () -> usersManager.deleteUser(userId));
    }

    @Test
    void UsersManagerTest_updateUser_success() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(STANDART_USER);
        when(userService.saveUser(STANDART_USER)).thenReturn(STANDART_USER);
        User actualUser = usersManager.updateUser(userId, STANDART_USER);
        assertEquals(STANDART_USER, actualUser);
    }

    @Test
    void UsersManagerTest_updateUser_userHasNotFound() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(null);
        assertThrows(CustomUserHasNotFoundException.class, () -> usersManager.deleteUser(userId));
    }

    @Test
    void UsersManagerTest_addUser_success() {
        when(userService.saveUser(STANDART_USER)).thenReturn(STANDART_USER);
        User actualUser = usersManager.addUser(STANDART_USER);
        assertEquals(STANDART_USER, actualUser);
    }

    @Test
    void UsersManagerTest_getUserStatistics_success() {
        List<ProTask> proTasks = Arrays.asList(
                new ProTask(1L, "task1", TaskStatus.COMPLETED, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(2L, "task2", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(3L, "task3", TaskStatus.EXPIRED, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000))
        );
        List<Task> tasks = Arrays.asList(
                new Task(1L, "task4", TaskStatus.COMPLETED, STANDART_USER),
                new Task(2L, "task5", TaskStatus.IN_PROGRESS, STANDART_USER),
                new Task(3L, "task6", TaskStatus.EXPIRED, STANDART_USER)
        );

        List<User> users = Arrays.asList(
                STANDART_USER,
                STANDART_PRO_USER
        );

        when(userService.getAllUsers()).thenReturn(users);
        when(proTaskService.getAllProTasks()).thenReturn(proTasks);
        when(taskService.getAllTasks()).thenReturn(tasks);

        String statistics = usersManager.getUsersStatistics();

        assertTrue(statistics.contains("Amount of users: 2"));
        assertTrue(statistics.contains("with PRO status: 1"));
        assertTrue(statistics.contains("with ADMIN status: 0"));
        assertTrue(statistics.contains("Amount of tasks: 6"));
        assertTrue(statistics.contains("COMPLETED: 2"));
        assertTrue(statistics.contains("IN PROGRESS: 2"));
        assertTrue(statistics.contains("EXPIRED: 2"));
    }
}
