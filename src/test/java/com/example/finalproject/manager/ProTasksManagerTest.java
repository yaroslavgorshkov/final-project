package com.example.finalproject.manager;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.User;
import com.example.finalproject.exception.CustomErrorJsonParseException;
import com.example.finalproject.exception.CustomTaskDoesntBelongToUserException;
import com.example.finalproject.exception.CustomUserHasNotFoundException;
import com.example.finalproject.service.ProTaskService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(ProTasksManager.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ProTasksManagerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProTaskService proTaskService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private ProTasksManager proTasksManager;

    private final User STANDART_PRO_USER = User.builder().username("proUser").password("password").id(1L).userRole(UserRole.PRO).build();
    private Principal STANDART_PRO_PRINCIPAL;

    @BeforeEach
    void initializePrincipal() {
        List<String> rolesString = new ArrayList<>();
        rolesString.add("PRO");
        List<SimpleGrantedAuthority> roles = rolesString.stream().map(SimpleGrantedAuthority::new).toList();
        STANDART_PRO_PRINCIPAL = new UsernamePasswordAuthenticationToken("proUser", null, roles);
    }

    @Test
    void ProTasksManagerTest_getAllProTasks_success() {
        List<ProTask> expectedProTasks = Arrays.asList(
                ProTask.builder().id(1L).description("task1").taskStatus(TaskStatus.IN_PROGRESS).creationTime(LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000)).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build(),
                ProTask.builder().id(2L).description("task2").taskStatus(TaskStatus.IN_PROGRESS).creationTime(LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000)).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build()
        );

        when(userService.findByUsername("proUser")).thenReturn(Optional.of(STANDART_PRO_USER));
        when(proTaskService.getAllTasksByUserId(1L)).thenReturn(expectedProTasks);
        List<ProTask> actualTasks = proTasksManager.getAllProTasks(STANDART_PRO_PRINCIPAL);
        assertEquals(expectedProTasks, actualTasks);
    }

    @Test
    void ProTasksManagerTest_getAllProTasks_UserNotFound() {
        when(userService.findByUsername("proUser")).thenReturn(Optional.empty());
        assertThrows(CustomUserHasNotFoundException.class, () -> proTasksManager.getAllProTasks(STANDART_PRO_PRINCIPAL));
    }

    @Test
    void ProTasksManagerTest_addProTask_success() {
        ProTaskCreationDto proTaskCreationDto = new ProTaskCreationDto("task", LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000));
        when(userService.findByUsername("proUser")).thenReturn(Optional.of(STANDART_PRO_USER));


        ProTask newProTask = ProTask.builder().description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).creationTime(LocalDateTime.now()).build();
        ProTask savedProTask = ProTask.builder().id(1L).description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).creationTime(LocalDateTime.now()).build();
        when(proTaskService.saveProTask(newProTask)).thenReturn(savedProTask);
        ProTask actualProTask = proTasksManager.addProTask(proTaskCreationDto, STANDART_PRO_PRINCIPAL);
        assertEquals(savedProTask, actualProTask);
    }

    @Test
    void ProTasksManagerTest_addProTask_ErrorJsonParse() {
        ProTaskCreationDto proTaskCreationDto = new ProTaskCreationDto("task", LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000));
        when(userService.findByUsername("proUser")).thenReturn(Optional.of(STANDART_PRO_USER));

        ProTask newProTask = ProTask.builder().description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).creationTime(LocalDateTime.now()).build();
        when(proTaskService.saveProTask(newProTask)).thenThrow(new DataIntegrityViolationException("Error json parse"));
        assertThrows(CustomErrorJsonParseException.class,() -> proTasksManager.addProTask(proTaskCreationDto, STANDART_PRO_PRINCIPAL));
    }

    @Test
    void ProTasksManagerTest_updateProTask_success() {
        Long taskId = 1L;
        TaskStatusUpdateDto taskStatusUpdateDto = new TaskStatusUpdateDto(TaskStatus.COMPLETED);
        ProTask existingProTask = ProTask.builder().id(taskId).description("task").taskStatus(TaskStatus.COMPLETED).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build();
        ProTask updatedProTask = ProTask.builder().id(taskId).description("task").taskStatus(TaskStatus.COMPLETED).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build();

        when(proTaskService.getProTaskById(1L)).thenReturn(existingProTask);
        when(userService.findByUsername("proUser")).thenReturn(Optional.of(STANDART_PRO_USER));
        when(proTaskService.saveProTask(existingProTask)).thenReturn(updatedProTask);
        ProTask actualProTask = proTasksManager.updateProTaskStatus(taskId, taskStatusUpdateDto, STANDART_PRO_PRINCIPAL);
        assertEquals(updatedProTask, actualProTask);
    }

    @Test
    void ProTasksManagerTest_updateProTask_TaskDoesntBelongToUser() {
        Long taskId = 1L;
        ProTask proTask = ProTask.builder().id(taskId).description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build();
        User badUser = User.builder().id(2L).username("badUser").build();
        Principal badPrincipal;
        List<String> rolesString = new ArrayList<>();
        rolesString.add("PRO");
        List<SimpleGrantedAuthority> roles = rolesString.stream().map(SimpleGrantedAuthority::new).toList();
        badPrincipal = new UsernamePasswordAuthenticationToken("badUser", null, roles);

        when(proTaskService.getProTaskById(1L)).thenReturn(proTask);

        when(userService.findByUsername("badUser")).thenReturn(Optional.of(badUser));

        assertThrows(CustomTaskDoesntBelongToUserException.class, () -> proTasksManager.updateProTaskStatus(taskId, any(), badPrincipal));
    }

    @Test
    void ProTasksManagerTest_deleteProTask_success() {
        Long taskId = 1L;
        ProTask proTask = ProTask.builder().id(taskId).description("task").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build();
        when(proTaskService.getProTaskById(1L)).thenReturn(proTask);
        when(userService.findByUsername("proUser")).thenReturn(Optional.of(STANDART_PRO_USER));
        proTasksManager.deleteProTask(taskId, STANDART_PRO_PRINCIPAL);
        verify(proTaskService, times(1)).deleteProTaskById(taskId);
    }

    @Test
    void ProTasksManagerTest_getTasksSortedByTime_success() {
        List<ProTask> proTasks = List.of(
                ProTask.builder().id(1L).description("task1").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).creationTime(LocalDateTime.now().minusDays(3L)).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build(),
                ProTask.builder().id(2L).description("task2").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build()
        );
        when(userService.findByUsername("proUser")).thenReturn(Optional.of(STANDART_PRO_USER));
        when(proTaskService.getAllTasksByUserId(1L)).thenReturn(proTasks);
        List<ProTask> actualTasksSortedByTime = proTasksManager.getTasksSortedByTime(STANDART_PRO_PRINCIPAL);

        assertEquals(proTasks, actualTasksSortedByTime);
    }

    @Test
    void ProTasksManagerTest_getTasksSortedByDeadline_success() {
        List<ProTask> proTasks = List.of(
                ProTask.builder().id(1L).description("task1").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build(),
                ProTask.builder().id(2L).description("task2").taskStatus(TaskStatus.IN_PROGRESS).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2026, 3, 15, 11, 21, 37, 535275000)).build()
        );
        when(userService.findByUsername("proUser")).thenReturn(Optional.of(STANDART_PRO_USER));
        when(proTaskService.getAllTasksByUserId(1L)).thenReturn(proTasks);
        List<ProTask> actualTasksSortedByDeadline = proTasksManager.getTasksSortedByDeadline(STANDART_PRO_PRINCIPAL);

        assertEquals(proTasks, actualTasksSortedByDeadline);
    }

    @Test
    void ProTasksManagerTest_getExpiredTasks_success() {
        List<ProTask> proTasks = List.of(
                ProTask.builder().id(1L).description("task1").taskStatus(TaskStatus.EXPIRED).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build(),
                ProTask.builder().id(2L).description("task2").taskStatus(TaskStatus.EXPIRED).user(STANDART_PRO_USER).creationTime(LocalDateTime.now()).deadline(LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)).build()
        );
        when(userService.findByUsername("proUser")).thenReturn(Optional.of(STANDART_PRO_USER));
        when(proTaskService.getAllTasksByUserId(1L)).thenReturn(proTasks);
        List<ProTask> actualExpiredTasks = proTasksManager.getExpiredTasks(STANDART_PRO_PRINCIPAL);

        assertEquals(proTasks, actualExpiredTasks);
    }
}