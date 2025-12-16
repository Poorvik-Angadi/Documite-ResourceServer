package com.documite.resourceserver.service;

import com.documite.resourceserver.model.Resource;
import com.documite.resourceserver.repository.ResourceRepository;
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
@DisplayName("Resource Service Tests")
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Resource testResource;

    @BeforeEach
    void setUp() {
        testResource = new Resource("Test Document", "A test document", "DOCUMENT", 1L);
        testResource.setId(1L);
    }

    @Test
    @DisplayName("Should create resource successfully")
    void testCreateResource_Success() {
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);

        Resource created = resourceService.createResource(testResource);

        assertNotNull(created);
        assertEquals("Test Document", created.getName());
        assertEquals("DOCUMENT", created.getType());
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    @DisplayName("Should get resource by ID successfully")
    void testGetResourceById_Success() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));

        Optional<Resource> found = resourceService.getResourceById(1L);

        assertTrue(found.isPresent());
        assertEquals("Test Document", found.get().getName());
        verify(resourceRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when resource not found by ID")
    void testGetResourceById_NotFound() {
        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Resource> found = resourceService.getResourceById(999L);

        assertFalse(found.isPresent());
        verify(resourceRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all resources successfully")
    void testGetAllResources_Success() {
        Resource resource2 = new Resource("Another Doc", "Description", "FILE", 2L);
        List<Resource> resources = Arrays.asList(testResource, resource2);

        when(resourceRepository.findAll()).thenReturn(resources);

        List<Resource> found = resourceService.getAllResources();

        assertEquals(2, found.size());
        verify(resourceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get resources by owner ID")
    void testGetResourcesByOwnerId() {
        List<Resource> resources = Arrays.asList(testResource);

        when(resourceRepository.findByOwnerId(1L)).thenReturn(resources);

        List<Resource> found = resourceService.getResourcesByOwnerId(1L);

        assertEquals(1, found.size());
        assertEquals(1L, found.get(0).getOwnerId());
        verify(resourceRepository, times(1)).findByOwnerId(1L);
    }

    @Test
    @DisplayName("Should get resources by type")
    void testGetResourcesByType() {
        List<Resource> resources = Arrays.asList(testResource);

        when(resourceRepository.findByType("DOCUMENT")).thenReturn(resources);

        List<Resource> found = resourceService.getResourcesByType("DOCUMENT");

        assertEquals(1, found.size());
        assertEquals("DOCUMENT", found.get(0).getType());
        verify(resourceRepository, times(1)).findByType("DOCUMENT");
    }

    @Test
    @DisplayName("Should search resources by name")
    void testSearchResourcesByName() {
        List<Resource> resources = Arrays.asList(testResource);

        when(resourceRepository.findByNameContaining("Test")).thenReturn(resources);

        List<Resource> found = resourceService.searchResourcesByName("Test");

        assertEquals(1, found.size());
        assertTrue(found.get(0).getName().contains("Test"));
        verify(resourceRepository, times(1)).findByNameContaining("Test");
    }

    @Test
    @DisplayName("Should update resource successfully")
    void testUpdateResource_Success() {
        Resource updateData = new Resource("Updated Name", "Updated desc", "UPDATED_TYPE", 1L);
        
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);

        Resource updated = resourceService.updateResource(1L, updateData);

        assertNotNull(updated);
        verify(resourceRepository, times(1)).findById(1L);
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent resource")
    void testUpdateResource_NotFound() {
        Resource updateData = new Resource("Updated Name", "Updated desc", "UPDATED_TYPE", 1L);
        
        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            resourceService.updateResource(999L, updateData);
        });

        verify(resourceRepository, times(1)).findById(999L);
        verify(resourceRepository, never()).save(any(Resource.class));
    }

    @Test
    @DisplayName("Should delete resource successfully")
    void testDeleteResource_Success() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        doNothing().when(resourceRepository).delete(testResource);

        assertDoesNotThrow(() -> resourceService.deleteResource(1L));

        verify(resourceRepository, times(1)).findById(1L);
        verify(resourceRepository, times(1)).delete(testResource);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent resource")
    void testDeleteResource_NotFound() {
        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            resourceService.deleteResource(999L);
        });

        verify(resourceRepository, times(1)).findById(999L);
        verify(resourceRepository, never()).delete(any(Resource.class));
    }

    @Test
    @DisplayName("Should check if resource exists by ID")
    void testExistsById() {
        when(resourceRepository.existsById(1L)).thenReturn(true);
        when(resourceRepository.existsById(999L)).thenReturn(false);

        assertTrue(resourceService.existsById(1L));
        assertFalse(resourceService.existsById(999L));

        verify(resourceRepository, times(1)).existsById(1L);
        verify(resourceRepository, times(1)).existsById(999L);
    }
}
