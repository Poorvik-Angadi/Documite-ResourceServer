package com.documite.resourceserver.controller;

import com.documite.resourceserver.model.Resource;
import com.documite.resourceserver.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    
    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        if (resource.getName() == null || resource.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (resource.getType() == null || resource.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (resource.getOwnerId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Resource createdResource = resourceService.createResource(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search) {
        
        List<Resource> resources;
        
        if (ownerId != null) {
            resources = resourceService.getResourcesByOwnerId(ownerId);
        } else if (type != null) {
            resources = resourceService.getResourcesByType(type);
        } else if (search != null) {
            resources = resourceService.searchResourcesByName(search);
        } else {
            resources = resourceService.getAllResources();
        }
        
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(
            @PathVariable Long id,
            @RequestBody Resource resourceDetails) {
        
        if (resourceDetails.getName() == null || resourceDetails.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Resource updatedResource = resourceService.updateResource(id, resourceDetails);
            return ResponseEntity.ok(updatedResource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        try {
            resourceService.deleteResource(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
