package com.documite.resourceserver.service;

import com.documite.resourceserver.model.User;
import com.documite.resourceserver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password123", "test@example.com", "USER");
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User created = userService.createUser(testUser);

        assertNotNull(created);
        assertEquals("testuser", created.getUsername());
        assertEquals("test@example.com", created.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate username")
    void testCreateUser_DuplicateUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(testUser);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    void testCreateUser_DuplicateEmail() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(testUser);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> found = userService.getUserById(1L);

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> found = userService.getUserById(999L);

        assertFalse(found.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void testGetUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> found = userService.getUserByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers_Success() {
        User user2 = new User("anotheruser", "password456", "another@example.com", "ADMIN");
        List<User> users = Arrays.asList(testUser, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> found = userService.getAllUsers();

        assertEquals(2, found.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() {
        User updateData = new User("testuser", "password123", "updated@example.com", "ADMIN");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updated = userService.updateUser(1L, updateData);

        assertNotNull(updated);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUser_NotFound() {
        User updateData = new User("testuser", "password", "test@example.com", "USER");
        
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, updateData);
        });

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999L);
        });

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }
}
