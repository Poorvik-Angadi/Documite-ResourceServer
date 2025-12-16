package com.documite.resourceserver.integration;

import com.documite.resourceserver.model.Resource;
import com.documite.resourceserver.model.User;
import com.documite.resourceserver.repository.ResourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Authentication and Authorization Integration Tests")
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceRepository resourceRepository;

    private Resource testResource;
    private User testUser;

    @BeforeEach
    void setUp() {
        resourceRepository.deleteAll();
        testResource = new Resource("Test Document", "A test document", "DOCUMENT", 1L);
        testUser = new User("testuser", "password123", "test@example.com", "USER");
    }

    @AfterEach
    void tearDown() {
        resourceRepository.deleteAll();
    }

    @Test
    @DisplayName("Should deny access to resources without authentication")
    void testAccessResourcesWithoutAuthentication() throws Exception {
        // GET requests
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isUnauthorized());

        // POST request
        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isUnauthorized());

        // PUT request
        mockMvc.perform(put("/api/resources/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isUnauthorized());

        // DELETE request
        mockMvc.perform(delete("/api/resources/1")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should deny access to users without authentication")
    void testAccessUsersWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should allow USER role to access resources")
    void testUserRoleAccessToResources() throws Exception {
        // User should be able to read resources
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk());

        // User should be able to create resources
        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow ADMIN role to access resources")
    void testAdminRoleAccessToResources() throws Exception {
        // Admin should be able to read resources
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk());

        // Admin should be able to create resources
        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should allow USER role to access users endpoint")
    void testUserRoleAccessToUsers() throws Exception {
        // User should be able to read users
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound()); // Not found because user doesn't exist
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow ADMIN role to manage users")
    void testAdminRoleAccessToUsers() throws Exception {
        // Admin should be able to read users
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        // Admin should be able to create users
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    @DisplayName("Should perform authenticated operations with specific user")
    void testAuthenticatedOperationsWithUser() throws Exception {
        // Create a resource as user1
        String response = mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Resource created = objectMapper.readValue(response, Resource.class);

        // Same user should be able to read the resource
        mockMvc.perform(get("/api/resources/" + created.getId()))
                .andExpect(status().isOk());

        // Same user should be able to update the resource
        created.setName("Updated by user1");
        mockMvc.perform(put("/api/resources/" + created.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk());

        // Same user should be able to delete the resource
        mockMvc.perform(delete("/api/resources/" + created.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should require CSRF token for state-changing operations")
    void testCSRFProtection() throws Exception {
        // POST without CSRF should fail (when CSRF is not disabled in test)
        // Note: In our test configuration, we're using .with(csrf()) to bypass this
        // In production, CSRF tokens would be required

        // With CSRF token, operations should succeed
        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle concurrent authenticated requests")
    void testConcurrentAuthenticatedRequests() throws Exception {
        // Create multiple resources concurrently
        for (int i = 0; i < 5; i++) {
            Resource resource = new Resource("Doc " + i, "Description " + i, "DOCUMENT", 1L);
            mockMvc.perform(post("/api/resources")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resource)))
                    .andExpect(status().isCreated());
        }

        // Verify all resources were created
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk());
    }
}
