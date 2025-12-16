package com.documite.resourceserver.controller;

import com.documite.resourceserver.model.User;
import com.documite.resourceserver.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password123", "test@example.com", "USER");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create a new user successfully")
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request when creating user with missing username")
    void testCreateUser_MissingUsername() throws Exception {
        User invalidUser = new User(null, "password", "test@example.com", "USER");

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request when creating user with missing password")
    void testCreateUser_MissingPassword() throws Exception {
        User invalidUser = new User("testuser", null, "test@example.com", "USER");

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request when creating user with missing email")
    void testCreateUser_MissingEmail() throws Exception {
        User invalidUser = new User("testuser", "password", null, "USER");

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return conflict when creating user with duplicate username")
    void testCreateUser_DuplicateUsername() throws Exception {
        when(userService.createUser(any(User.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return conflict when creating user with duplicate email")
    void testCreateUser_DuplicateEmail() throws Exception {
        when(userService.createUser(any(User.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get user by ID successfully")
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return not found when user does not exist")
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all users successfully")
    void testGetAllUsers_Success() throws Exception {
        User user2 = new User("anotheruser", "password456", "another@example.com", "ADMIN");
        user2.setId(2L);
        List<User> users = Arrays.asList(testUser, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("anotheruser"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() throws Exception {
        User updatedUser = new User("testuser", "password123", "updated@example.com", "ADMIN");
        updatedUser.setId(1L);

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return not found when updating non-existent user")
    void testUpdateUser_NotFound() throws Exception {
        User updateData = new User("testuser", "password", "test@example.com", "USER");

        when(userService.updateUser(eq(999L), any(User.class)))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        mockMvc.perform(put("/api/users/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(999L), any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return not found when deleting non-existent user")
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new RuntimeException("User not found with id: 999"))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(999L);
    }

    @Test
    @DisplayName("Should return unauthorized when accessing without authentication")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}
