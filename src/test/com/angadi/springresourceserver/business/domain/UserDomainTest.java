package com.angadi.springresourceserver.business.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api. Assertions.*;

class UsersDomainTest {

    @Test
    void testUsersDomainAllArgsConstructor() {
        // Arrange & Act
        UsersDomain user = new UsersDomain("testUser", 1L);

        // Assert
        assertEquals("testUser", user.getUserName());
        assertEquals(1L, user.getUserID());
    }

    @Test
    void testUsersDomainNoArgsConstructor() {
        // Act
        UsersDomain user = new UsersDomain();

        // Assert
        assertNotNull(user);
    }

    @Test
    void testUsersDomainSetters() {
        // Arrange
        UsersDomain user = new UsersDomain();

        // Act
        user.setUserName("newUser");
        user.setUserID(2L);

        // Assert
        assertEquals("newUser", user.getUserName());
        assertEquals(2L, user.getUserID());
    }
}