package com.documite.resourceserver.repository;

import com.documite.resourceserver.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByOwnerId(Long ownerId);
    List<Resource> findByType(String type);
    List<Resource> findByNameContaining(String name);
}
