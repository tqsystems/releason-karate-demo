package com.example.controller;

import com.example.entity.Comment;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Comment management
 * Provides CRUD operations for comments
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all comments
     * Optionally filter by postId
     */
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments(@RequestParam(required = false) UUID postId) {
        logger.info("GET /api/comments - Fetching comments" + (postId != null ? " for post: " + postId : ""));
        
        List<Comment> comments;
        if (postId != null) {
            comments = commentRepository.findByPostId(postId);
        } else {
            comments = commentRepository.findAll();
        }
        
        return ResponseEntity.ok(comments);
    }

    /**
     * Get comment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable UUID id) {
        logger.info("GET /api/comments/{} - Fetching comment by ID", id);
        return commentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Comment not found with id: " + id));
    }

    /**
     * Create new comment
     * Validates that post and user exist
     */
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody Comment comment) {
        logger.info("POST /api/comments - Creating new comment on post: {}", comment.getPostId());
        
        // Validate post exists
        if (!postRepository.existsById(comment.getPostId())) {
            logger.warn("Post not found: {}", comment.getPostId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Post not found with id: " + comment.getPostId());
        }

        // Validate user exists
        if (!userRepository.existsById(comment.getUserId())) {
            logger.warn("User not found: {}", comment.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User not found with id: " + comment.getUserId());
        }

        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully with ID: {}", savedComment.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    /**
     * Delete comment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID id) {
        logger.info("DELETE /api/comments/{} - Deleting comment", id);
        
        return commentRepository.findById(id)
                .map(comment -> {
                    commentRepository.delete(comment);
                    logger.info("Comment deleted successfully: {}", id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
