package com.example.repository;

import com.example.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Comment entity
 * Provides CRUD operations and custom queries
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    
    /**
     * Find all comments on a specific post
     */
    List<Comment> findByPostId(UUID postId);
    
    /**
     * Find all comments by a specific user
     */
    List<Comment> findByUserId(UUID userId);
}
