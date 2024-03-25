package com.example.finalproject.manager;

import com.example.finalproject.dto.TaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.exception.CustomErrorJsonParseException;
import com.example.finalproject.exception.CustomTaskDoesntBelongToUserException;
import com.example.finalproject.exception.CustomUserHasNotFoundException;
import com.example.finalproject.service.TaskService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.util.JwtTokenUtils;
import com.example.finalproject.util.TaskStatus;
import com.example.finalproject.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(UserTasksManager.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserTasksManagerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UserTasksManager userTasksManager;

    private final User STANDART_USER = User.builder().username("user").password("password").id(1L).userRole(UserRole.USER).build();
    private Principal STANDART_PRINCIPAL;

    @BeforeEach
    void initializePrincipal() {
        List<String> rolesString = new ArrayList<>();
        rolesString.add("USER");
        List<SimpleGrantedAuthority> roles = rolesString.stream().map(SimpleGrantedAuthority::new).toList();
        STANDART_PRINCIPAL = new UsernamePasswordAuthenticationToken("user", null, roles);
    }

    @Test
    void UserTasksManagerTest_getAllTasks_success() {
        List<Task> expectedTasks = Arrays.asList(
                Task.builder().id(1L).description("task1").taskStatus(TaskStatus.IN_PROGRESS).build(),
                Task.builder().id(2L).description("task2").taskStatus(TaskStatus.IN_PROGRESS).build()
        );

        when(userService.findByUsername("user")).thenReturn(Optional.of(STANDART_USER));
        when(taskService.getAllTasksByUserId(1L)).thenReturn(expectedTasks);
        List<Task> actualTasks = userTasksManager.getAllTasks(STANDART_PRINCIPAL);
        assertEquals(expectedTasks, actualTasks);
    }

    @Test
    void UserTasksManagerTest_getAllTasks_UserNotFound() {
        when(userService.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(CustomUserHasNotFoundException.class, () -> userTasksManager.getAllTasks(STANDART_PRINCIPAL));
    }

    @Test
    void UserTasksManagerTest_addTask_success() {
        TaskCreationDto taskCreationDto = new TaskCreationDto("task");
        when(userService.findByUsername("user")).thenReturn(Optional.of(STANDART_USER));
        Task newTask = Task.builder().description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_USER).build();
        Task savedTask = Task.builder().id(1L).description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_USER).build();
        when(taskService.saveTask(newTask)).thenReturn(savedTask);
        Task actualTask = userTasksManager.addTask(taskCreationDto, STANDART_PRINCIPAL);
        assertEquals(savedTask, actualTask);
    }

    @Test
    void UserTasksManagerTest_addTask_ErrorJsonParse() {
        TaskCreationDto taskCreationDto = new TaskCreationDto("task");
        Task newTask = Task.builder().description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_USER).build();
        when(userService.findByUsername("user")).thenReturn(Optional.of(STANDART_USER));
        when(taskService.saveTask(newTask)).thenThrow(new DataIntegrityViolationException("Error converting JSON format"));
        assertThrows(CustomErrorJsonParseException.class, () -> userTasksManager.addTask(taskCreationDto, STANDART_PRINCIPAL));
    }

    @Test
    void UserTasksManagerTest_updateTask_success() {
        Long taskId = 1L;
        TaskStatusUpdateDto taskStatusUpdateDto = new TaskStatusUpdateDto(TaskStatus.COMPLETED);
        Task existingTask = Task.builder().id(taskId).description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_USER).build();
        Task updatedTask = Task.builder().id(taskId).description("task").taskStatus(TaskStatus.COMPLETED).user(STANDART_USER).build();

        when(taskService.getTaskById(1L)).thenReturn(existingTask);
        when(userService.findByUsername("user")).thenReturn(Optional.of(STANDART_USER));
        when(taskService.saveTask(existingTask)).thenReturn(updatedTask);
        Task actualTask = userTasksManager.updateTaskStatus(taskId, taskStatusUpdateDto, STANDART_PRINCIPAL);
        assertEquals(updatedTask, actualTask);
    }

    @Test
    void UserTasksManagerTest_updateTask_TaskDoesntBelongToUser() {
        Long taskId = 1L;
        Task task = Task.builder().id(taskId).description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_USER).build();
        User badUser = User.builder().id(2L).username("badUser").build();
        Principal badPrincipal;
        List<String> rolesString = new ArrayList<>();
        rolesString.add("USER");
        List<SimpleGrantedAuthority> roles = rolesString.stream().map(SimpleGrantedAuthority::new).toList();
        badPrincipal = new UsernamePasswordAuthenticationToken("badUser", null, roles);

        when(taskService.getTaskById(1L)).thenReturn(task);

        when(userService.findByUsername("badUser")).thenReturn(Optional.of(badUser));

        assertThrows(CustomTaskDoesntBelongToUserException.class, () -> userTasksManager.updateTaskStatus(taskId, any(), badPrincipal));
    }

    @Test
    void UserTasksManagerTest_deleteTask_success() {
        Long taskId = 1L;
        Task task = Task.builder().id(taskId).description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_USER).build();
        when(taskService.getTaskById(1L)).thenReturn(task);
        when(userService.findByUsername("user")).thenReturn(Optional.of(STANDART_USER));
        userTasksManager.deleteTask(taskId, STANDART_PRINCIPAL);
        verify(taskService, times(1)).deleteTaskById(taskId);
    }
}