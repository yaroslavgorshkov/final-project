package com.example.finalproject.controller;

import com.example.finalproject.dto.UserResponseInfoDto;
import com.example.finalproject.entity.User;
import com.example.finalproject.manager.UsersManager;
import com.example.finalproject.util.JwtTokenUtils;
import com.example.finalproject.util.ObjectMapperUtil;
import com.example.finalproject.util.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersManager usersManager;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @InjectMocks
    private UserController userController;

    @Test
    void UserController_getAllUsers_success() throws Exception {
        List<UserResponseInfoDto> users = Arrays.asList(
                new UserResponseInfoDto(1L, "user1", UserRole.USER),
                new UserResponseInfoDto(2L, "user2", UserRole.USER)
        );

        when(usersManager.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/api/admin/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'id'  :   1,'username':'user1','userRole':'USER'},{'id':2,'username':'user2','userRole':'USER'}]"));
    }

    @Test
    void UserController_deleteUser_success() throws Exception {
        Long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/api/admin/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void UserController_updateUser_success() throws Exception {
        Long userId = 1L;
        User user = User.builder().username("user1").password("password1").userRole(UserRole.USER).build();
        when(usersManager.updateUser(userId, user)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/api/admin/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'username':'user1', 'password':'password1', 'userRole':'USER'}"));
    }

    @Test
    void UserController_addUser_success() throws Exception {
        User user = User.builder().id(1L).username("user1").password("password1").userRole(UserRole.USER).build();

        when(usersManager.addUser(user)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectMapperUtil.asJsonString(user)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json("{'id':1,'username':'user1', 'password':'password1', 'userRole':'USER'}"));
    }

    @Test
    void testGetUsersStatistics() throws Exception {
        String statistics = "mocked statistics";

        when(usersManager.getUsersStatistics()).thenReturn(statistics);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/api/admin/users/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().string(statistics));
    }
}
