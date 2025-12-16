package com.documite.resourceserver.controller;

import com.documite.resourceserver.model.Resource;
import com.documite.resourceserver.service.ResourceService;
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

@WebMvcTest(ResourceController.class)
@DisplayName("Resource Controller Tests")
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourceService resourceService;

    private Resource testResource;

    @BeforeEach
    void setUp() {
        testResource = new Resource("Test Document", "A test document", "DOCUMENT", 1L);
        testResource.setId(1L);
        testResource.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create a new resource successfully")
    void testCreateResource_Success() throws Exception {
        when(resourceService.createResource(any(Resource.class))).thenReturn(testResource);

        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResource)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Document"))
                .andExpect(jsonPath("$.type").value("DOCUMENT"))
                .andExpect(jsonPath("$.ownerId").value(1L));

        verify(resourceService, times(1)).createResource(any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return bad request when creating resource with missing name")
    void testCreateResource_MissingName() throws Exception {
        Resource invalidResource = new Resource(null, "Description", "DOCUMENT", 1L);

        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResource)))
                .andExpect(status().isBadRequest());

        verify(resourceService, never()).createResource(any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return bad request when creating resource with missing type")
    void testCreateResource_MissingType() throws Exception {
        Resource invalidResource = new Resource("Test", "Description", null, 1L);

        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResource)))
                .andExpect(status().isBadRequest());

        verify(resourceService, never()).createResource(any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return bad request when creating resource with missing ownerId")
    void testCreateResource_MissingOwnerId() throws Exception {
        Resource invalidResource = new Resource("Test", "Description", "DOCUMENT", null);

        mockMvc.perform(post("/api/resources")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResource)))
                .andExpect(status().isBadRequest());

        verify(resourceService, never()).createResource(any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get resource by ID successfully")
    void testGetResourceById_Success() throws Exception {
        when(resourceService.getResourceById(1L)).thenReturn(Optional.of(testResource));

        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Document"))
                .andExpect(jsonPath("$.type").value("DOCUMENT"));

        verify(resourceService, times(1)).getResourceById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return not found when resource does not exist")
    void testGetResourceById_NotFound() throws Exception {
        when(resourceService.getResourceById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/resources/999"))
                .andExpect(status().isNotFound());

        verify(resourceService, times(1)).getResourceById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all resources successfully")
    void testGetAllResources_Success() throws Exception {
        Resource resource2 = new Resource("Another Doc", "Another description", "FILE", 2L);
        resource2.setId(2L);
        List<Resource> resources = Arrays.asList(testResource, resource2);

        when(resourceService.getAllResources()).thenReturn(resources);

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test Document"))
                .andExpect(jsonPath("$[1].name").value("Another Doc"));

        verify(resourceService, times(1)).getAllResources();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should filter resources by owner ID")
    void testGetResourcesByOwnerId() throws Exception {
        List<Resource> resources = Arrays.asList(testResource);

        when(resourceService.getResourcesByOwnerId(1L)).thenReturn(resources);

        mockMvc.perform(get("/api/resources?ownerId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ownerId").value(1L));

        verify(resourceService, times(1)).getResourcesByOwnerId(1L);
        verify(resourceService, never()).getAllResources();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should filter resources by type")
    void testGetResourcesByType() throws Exception {
        List<Resource> resources = Arrays.asList(testResource);

        when(resourceService.getResourcesByType("DOCUMENT")).thenReturn(resources);

        mockMvc.perform(get("/api/resources?type=DOCUMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("DOCUMENT"));

        verify(resourceService, times(1)).getResourcesByType("DOCUMENT");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should search resources by name")
    void testSearchResourcesByName() throws Exception {
        List<Resource> resources = Arrays.asList(testResource);

        when(resourceService.searchResourcesByName("Test")).thenReturn(resources);

        mockMvc.perform(get("/api/resources?search=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(resourceService, times(1)).searchResourcesByName("Test");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should update resource successfully")
    void testUpdateResource_Success() throws Exception {
        Resource updatedResource = new Resource("Updated Name", "Updated description", "UPDATED_TYPE", 1L);
        updatedResource.setId(1L);

        when(resourceService.updateResource(eq(1L), any(Resource.class))).thenReturn(updatedResource);

        mockMvc.perform(put("/api/resources/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.type").value("UPDATED_TYPE"));

        verify(resourceService, times(1)).updateResource(eq(1L), any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return bad request when updating with empty name")
    void testUpdateResource_EmptyName() throws Exception {
        Resource invalidResource = new Resource("", "Description", "DOCUMENT", 1L);

        mockMvc.perform(put("/api/resources/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResource)))
                .andExpect(status().isBadRequest());

        verify(resourceService, never()).updateResource(anyLong(), any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return not found when updating non-existent resource")
    void testUpdateResource_NotFound() throws Exception {
        Resource updateData = new Resource("Updated", "Description", "DOCUMENT", 1L);

        when(resourceService.updateResource(eq(999L), any(Resource.class)))
                .thenThrow(new RuntimeException("Resource not found with id: 999"));

        mockMvc.perform(put("/api/resources/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(resourceService, times(1)).updateResource(eq(999L), any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should delete resource successfully")
    void testDeleteResource_Success() throws Exception {
        doNothing().when(resourceService).deleteResource(1L);

        mockMvc.perform(delete("/api/resources/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(resourceService, times(1)).deleteResource(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return not found when deleting non-existent resource")
    void testDeleteResource_NotFound() throws Exception {
        doThrow(new RuntimeException("Resource not found with id: 999"))
                .when(resourceService).deleteResource(999L);

        mockMvc.perform(delete("/api/resources/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(resourceService, times(1)).deleteResource(999L);
    }

    @Test
    @DisplayName("Should return unauthorized when accessing without authentication")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isUnauthorized());
    }
}
