package com.example.repository;

import com.example.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Post entity
 * Provides CRUD operations and custom queries
 */
@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    
    /**
     * Find all posts by a specific user
     */
    List<Post> findByUserId(UUID userId);
}
