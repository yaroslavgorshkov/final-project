package com.example.finalproject.controller;

import com.example.finalproject.dto.ProTaskCreationDto;
import com.example.finalproject.dto.TaskCreationDto;
import com.example.finalproject.dto.TaskStatusUpdateDto;
import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.exception.CustomErrorJsonParseException;
import com.example.finalproject.exception.CustomTaskDoesntBelongToUserException;
import com.example.finalproject.exception.CustomTaskHasNotFoundException;
import com.example.finalproject.exception.CustomUserHasNotFoundException;
import com.example.finalproject.manager.ProTasksManager;
import com.example.finalproject.manager.UserTasksManager;
import com.example.finalproject.util.JwtTokenUtils;
import com.example.finalproject.util.ObjectMapperUtil;
import com.example.finalproject.util.TaskStatus;
import com.example.finalproject.util.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(UserTasksController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserTasksControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProTasksManager proTasksManager;

    @MockBean
    private UserTasksManager userTasksManager;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @InjectMocks
    private UserTasksController userTasksController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final User STANDART_USER = User.builder().id(1L).username("user1").password("password1").userRole(UserRole.USER).build();
    private static final User STANDART_PRO_USER = User.builder().id(1L).username("user2").password("password2").userRole(UserRole.PRO).build();


    @Test
    void UserTasksControllerTest_getAllTasks_ProRole_success() throws Exception {
        List<ProTask> proTasks = new ArrayList<>(List.of(
                new ProTask(1L, "desk1", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(2L, "desk2", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000))
        ));
        Principal principal = createPrincipalWithRole("PRO");
        when(proTasksManager.getAllProTasks(principal)).thenReturn(proTasks);

        mockMvc.perform(get("/v1/api/tasks").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'id':1,'description':'desk1','taskStatus':'IN_PROGRESS','creationTime':'2024-03-15T11:21:37.535275','deadline': '2025-03-15T11:21:37.535275'},{'id':2,'description':'desk2','taskStatus':'IN_PROGRESS','creationTime':'2024-03-15T11:21:37.535275','deadline': '2025-03-15T11:21:37.535275'}]"));
    }

    @Test
    void UserTasksControllerTest_getAllTasks_UserRole_success() throws Exception {
        List<Task> tasks = new ArrayList<>(List.of(
                new Task(1L, "desk1", TaskStatus.IN_PROGRESS, STANDART_USER),
                new Task(2L, "desk2", TaskStatus.IN_PROGRESS, STANDART_USER)
        ));
        Principal principal = createPrincipalWithRole("USER");
        when(userTasksManager.getAllTasks(principal)).thenReturn(tasks);

        mockMvc.perform(get("/v1/api/tasks").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'id':1,'description':'desk1','taskStatus':'IN_PROGRESS'},{'id':2,'description':'desk2','taskStatus':'IN_PROGRESS'}]"));
    }

    @Test
    void UserTasksControllerTest_addTask_ProRole_success() throws Exception {
        ProTaskCreationDto proTaskCreationDto = new ProTaskCreationDto("desk1", LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000));
        Principal principal = createPrincipalWithRole("PRO");
        ProTask createdProTask = new ProTask(1L, "desk1", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000));

        when(proTasksManager.addProTask(proTaskCreationDto, principal)).thenReturn(createdProTask);

        mockMvc.perform(post("/v1/api/tasks")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(proTaskCreationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'id':1,'description':'desk1','taskStatus':'IN_PROGRESS','creationTime':'2024-03-15T11:21:37.535275','deadline': '2025-03-15T11:21:37.535275'}"));
    }

    @Test
    void UserTasksControllerTest_addTask_UserRole_success() throws Exception {
        TaskCreationDto taskCreationDto = new TaskCreationDto("desk1");
        Principal principal = createPrincipalWithRole("USER");
        Task createdTask = new Task(1L, "desk1", TaskStatus.IN_PROGRESS, STANDART_USER);

        when(userTasksManager.addTask(taskCreationDto, principal)).thenReturn(createdTask);

        mockMvc.perform(post("/v1/api/tasks")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(taskCreationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'id':1,'description':'desk1','taskStatus':'IN_PROGRESS'}"));
    }

    @Test
    void UserTasksControllerTest_updateTaskStatus_ProRole_success() throws Exception {
        Long taskId = 1L;
        ProTask updatedProTask = new ProTask(1L, "desk1", TaskStatus.COMPLETED, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000));
        TaskStatusUpdateDto taskStatusUpdateDto = new TaskStatusUpdateDto(TaskStatus.COMPLETED);
        Principal principal = createPrincipalWithRole("PRO");
        when(proTasksManager.updateProTaskStatus(taskId, taskStatusUpdateDto, principal)).thenReturn(updatedProTask);

        mockMvc.perform(put("/v1/api/tasks/{taskId}", taskId)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(taskStatusUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'id':1,'description':'desk1','taskStatus':'COMPLETED','creationTime':'2024-03-15T11:21:37.535275','deadline': '2025-03-15T11:21:37.535275'}"));
    }

    @Test
    void UserTasksControllerTest_updateTaskStatus_UserRole_success() throws Exception {
        Long taskId = 1L;
        Task updatedTask = new Task(1L, "desk1", TaskStatus.COMPLETED, STANDART_USER);
        TaskStatusUpdateDto taskStatusUpdateDto = new TaskStatusUpdateDto(TaskStatus.COMPLETED);
        Principal principal = createPrincipalWithRole("USER");
        when(userTasksManager.updateTaskStatus(taskId, taskStatusUpdateDto, principal)).thenReturn(updatedTask);

        mockMvc.perform(put("/v1/api/tasks/{taskId}", taskId)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(taskStatusUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'id':1,'description':'desk1','taskStatus':'COMPLETED'}"));
    }

    @Test
    void UserTasksControllerTest_deleteTask_ProRole_success() throws Exception {
        Long taskId = 1L;
        Principal principal = createPrincipalWithRole("PRO");
        mockMvc.perform(delete("/v1/api/tasks/{taskId}", taskId).principal(principal))
                .andExpect(status().isNoContent());
    }

    @Test
    void UserTasksControllerTest_deleteTask_UserRole_success() throws Exception {
        Long taskId = 1L;
        Principal principal = createPrincipalWithRole("USER");
        mockMvc.perform(delete("/v1/api/tasks/{taskId}", taskId).principal(principal))
                .andExpect(status().isNoContent());
    }

    @Test
    void UserTasksControllerTest_getTasksSortedByTime_ProRole_success() throws Exception {
        List<ProTask> proTaskList = new ArrayList<>(List.of(
                new ProTask(1L, "desk1", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2026, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(2L, "desk2", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2026, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(3L, "desk3", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2023, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2026, 3, 15, 11, 21, 37, 535275000))
        ));

        Principal principal = createPrincipalWithRole("PRO");
        when(proTasksManager.getTasksSortedByTime(principal)).thenReturn(proTaskList);

        mockMvc.perform(get("/v1/api/tasks/by-time").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'id':2,'description':'desk2','taskStatus':'IN_PROGRESS','creationTime':'2025-03-15T11:21:37.535275','deadline': '2026-03-15T11:21:37.535275'},{'id':1,'description':'desk1','taskStatus':'IN_PROGRESS','creationTime':'2024-03-15T11:21:37.535275','deadline': '2026-03-15T11:21:37.535275'}, {'id':3,'description':'desk3','taskStatus':'IN_PROGRESS','creationTime':'2023-03-15T11:21:37.535275','deadline': '2026-03-15T11:21:37.535275'}]"));
    }

    @Test
    void UserTasksControllerTest_getTasksSortedByDeadline_ProRole_success() throws Exception {
        List<ProTask> proTaskList = new ArrayList<>(List.of(
                new ProTask(1L, "desk1", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(2L, "desk2", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2027, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(3L, "desk3", TaskStatus.IN_PROGRESS, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2026, 3, 15, 11, 21, 37, 535275000))
        ));

        Principal principal = createPrincipalWithRole("PRO");
        when(proTasksManager.getTasksSortedByDeadline(principal)).thenReturn(proTaskList);

        mockMvc.perform(get("/v1/api/tasks/deadline-soon").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'id':1,'description':'desk1','taskStatus':'IN_PROGRESS','creationTime':'2024-03-15T11:21:37.535275','deadline': '2025-03-15T11:21:37.535275'},{'id':3,'description':'desk3','taskStatus':'IN_PROGRESS','creationTime':'2024-03-15T11:21:37.535275','deadline': '2026-03-15T11:21:37.535275'}, {'id':2,'description':'desk2','taskStatus':'IN_PROGRESS','creationTime':'2024-03-15T11:21:37.535275','deadline': '2027-03-15T11:21:37.535275'}]"));
    }

    @Test
    void UserTasksControllerTest_getExpiredTasks_ProRole_success() throws Exception {
        List<ProTask> proTaskListWithExpiredTasks = new ArrayList<>(List.of(
                new ProTask(2L, "desk2", TaskStatus.EXPIRED, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000)),
                new ProTask(3L, "desk3", TaskStatus.EXPIRED, STANDART_PRO_USER, LocalDateTime.of(2024, 3, 15, 11, 21, 37, 535275000), LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000))
        ));

        Principal principal = createPrincipalWithRole("PRO");
        when(proTasksManager.getExpiredTasks(principal)).thenReturn(proTaskListWithExpiredTasks);

        mockMvc.perform(get("/v1/api/tasks/expired").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("["
                        + "{'id':2,'description':'desk2','taskStatus':'EXPIRED','creationTime':'2024-03-15T11:21:37.535275','deadline': '2025-03-15T11:21:37.535275'},"
                        + "{'id':3,'description':'desk3','taskStatus':'EXPIRED','creationTime':'2024-03-15T11:21:37.535275','deadline': '2025-03-15T11:21:37.535275'}]"));
    }

    @Test
    void UserTasksControllerTest_getAllTasks_UserHasNotFound() throws Exception {
        Principal principal = createPrincipalWithRole("PRO");
        when(proTasksManager.getAllProTasks(any())).thenThrow(new CustomUserHasNotFoundException("User has not found"));

        mockMvc.perform(get("/v1/api/tasks").principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    void UserTasksControllerTest_addTask_ErrorJsonParse() throws Exception {
        ProTaskCreationDto proTaskCreationDto = new ProTaskCreationDto("desk1", LocalDateTime.of(2025, 3, 15, 11, 21, 37, 535275000));
        Principal principal = createPrincipalWithRole("PRO");
        when(proTasksManager.addProTask(proTaskCreationDto, principal)).thenThrow(new CustomErrorJsonParseException("Error json parsing"));

        mockMvc.perform(post("/v1/api/tasks").principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(proTaskCreationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void UserTasksControllerTest_updateTaskStatus_TaskDoesntBelongToUser() throws Exception {
        Long taskId = 1L;
        TaskStatusUpdateDto taskStatusUpdateDto = new TaskStatusUpdateDto(TaskStatus.COMPLETED);
        Principal principal = createPrincipalWithRole("PRO");
        when(proTasksManager.updateProTaskStatus(taskId, taskStatusUpdateDto, principal)).thenThrow(new CustomTaskDoesntBelongToUserException("Pro task with id = " + taskId + " does not belong to user"));

        mockMvc.perform(put("/v1/api/tasks/{taskId}", taskId).principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatusUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void UserTasksControllerTest_deleteTask_TaskHasNotFound() throws Exception {
        Long taskId = 1L;
        Principal principal = createPrincipalWithRole("PRO");

        doThrow(new CustomTaskHasNotFoundException("Pro task with id = " + taskId + " has not found"))
                .when(proTasksManager).deleteProTask(taskId, principal);

        mockMvc.perform(delete("/v1/api/tasks/{taskId}", taskId)
                        .principal(principal))
                .andExpect(status().isNotFound());
    }

    private Principal createPrincipalWithRole(String role) {
        List<String> rolesString = new ArrayList<>();
        rolesString.add(role);
        List<SimpleGrantedAuthority> roles = rolesString.stream().map(SimpleGrantedAuthority::new).toList();
        return new UsernamePasswordAuthenticationToken("user", null, roles);
    }
}
