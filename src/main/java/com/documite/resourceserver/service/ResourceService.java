package com.documite.resourceserver.service;

import com.documite.resourceserver.model.Resource;
import com.documite.resourceserver.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    
    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public List<Resource> getResourcesByOwnerId(Long ownerId) {
        return resourceRepository.findByOwnerId(ownerId);
    }

    public List<Resource> getResourcesByType(String type) {
        return resourceRepository.findByType(type);
    }

    public List<Resource> searchResourcesByName(String name) {
        return resourceRepository.findByNameContaining(name);
    }

    public Resource updateResource(Long id, Resource resourceDetails) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
        
        resource.setName(resourceDetails.getName());
        resource.setDescription(resourceDetails.getDescription());
        resource.setType(resourceDetails.getType());
        
        return resourceRepository.save(resource);
    }

    public void deleteResource(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
        resourceRepository.delete(resource);
    }

    public boolean existsById(Long id) {
        return resourceRepository.existsById(id);
    }
}
