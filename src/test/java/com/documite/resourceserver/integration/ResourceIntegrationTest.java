package com.documite.resourceserver.integration;

import com.documite.resourceserver.model.Resource;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Resource Integration Tests")
class ResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceRepository resourceRepository;

    private Resource testResource;

    @BeforeEach
    void setUp() {
        resourceRepository.deleteAll();
        testResource = new Resource("Integration Test Doc", "A test document for integration", "DOCUMENT", 1L);
    }

    @AfterEach
    void tearDown() {
        resourceRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should perform full CRUD operations on resources")
    void testFullCRUDOperations() throws Exception {
        // CREATE
        String createResponse = mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Doc"))
                .andExpect(jsonPath("$.type").value("DOCUMENT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Resource createdResource = objectMapper.readValue(createResponse, Resource.class);
        Long resourceId = createdResource.getId();

        // READ by ID
        mockMvc.perform(get("/api/resources/" + resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceId))
                .andExpect(jsonPath("$.name").value("Integration Test Doc"));

        // READ all
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Integration Test Doc"));

        // UPDATE
        createdResource.setName("Updated Integration Test Doc");
        createdResource.setDescription("Updated description");

        mockMvc.perform(put("/api/resources/" + resourceId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Integration Test Doc"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        // DELETE
        mockMvc.perform(delete("/api/resources/" + resourceId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/resources/" + resourceId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should filter resources by owner ID")
    void testFilterByOwnerId() throws Exception {
        Resource resource1 = new Resource("Resource 1", "Description 1", "DOCUMENT", 1L);
        Resource resource2 = new Resource("Resource 2", "Description 2", "FILE", 2L);
        Resource resource3 = new Resource("Resource 3", "Description 3", "DOCUMENT", 1L);

        resourceRepository.save(resource1);
        resourceRepository.save(resource2);
        resourceRepository.save(resource3);

        mockMvc.perform(get("/api/resources?ownerId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ownerId").value(1))
                .andExpect(jsonPath("$[1].ownerId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should filter resources by type")
    void testFilterByType() throws Exception {
        Resource resource1 = new Resource("Doc 1", "Description 1", "DOCUMENT", 1L);
        Resource resource2 = new Resource("File 1", "Description 2", "FILE", 2L);
        Resource resource3 = new Resource("Doc 2", "Description 3", "DOCUMENT", 1L);

        resourceRepository.save(resource1);
        resourceRepository.save(resource2);
        resourceRepository.save(resource3);

        mockMvc.perform(get("/api/resources?type=DOCUMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("DOCUMENT"))
                .andExpect(jsonPath("$[1].type").value("DOCUMENT"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should search resources by name")
    void testSearchByName() throws Exception {
        Resource resource1 = new Resource("Important Document", "Description 1", "DOCUMENT", 1L);
        Resource resource2 = new Resource("Regular File", "Description 2", "FILE", 2L);
        Resource resource3 = new Resource("Important Report", "Description 3", "DOCUMENT", 1L);

        resourceRepository.save(resource1);
        resourceRepository.save(resource2);
        resourceRepository.save(resource3);

        mockMvc.perform(get("/api/resources?search=Important"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should validate required fields on create")
    void testValidationOnCreate() throws Exception {
        // Missing name
        Resource invalidResource1 = new Resource(null, "Description", "DOCUMENT", 1L);
        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResource1)))
                .andExpect(status().isBadRequest());

        // Missing type
        Resource invalidResource2 = new Resource("Name", "Description", null, 1L);
        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResource2)))
                .andExpect(status().isBadRequest());

        // Missing ownerId
        Resource invalidResource3 = new Resource("Name", "Description", "DOCUMENT", null);
        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResource3)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return unauthorized when not authenticated")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle non-existent resource operations")
    void testNonExistentResourceOperations() throws Exception {
        // Get non-existent resource
        mockMvc.perform(get("/api/resources/99999"))
                .andExpect(status().isNotFound());

        // Update non-existent resource
        Resource updateData = new Resource("Updated", "Description", "DOCUMENT", 1L);
        mockMvc.perform(put("/api/resources/99999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        // Delete non-existent resource
        mockMvc.perform(delete("/api/resources/99999")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
