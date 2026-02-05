package com.example.controller;

import com.example.entity.Post;
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
 * REST Controller for Post management
 * Provides CRUD operations for posts
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all posts
     * Optionally filter by userId
     */
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(required = false) UUID userId) {
        logger.info("GET /api/posts - Fetching posts" + (userId != null ? " for user: " + userId : ""));
        
        List<Post> posts;
        if (userId != null) {
            posts = postRepository.findByUserId(userId);
        } else {
            posts = postRepository.findAll();
        }
        
        return ResponseEntity.ok(posts);
    }

    /**
     * Get post by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable UUID id) {
        logger.info("GET /api/posts/{} - Fetching post by ID", id);
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Post not found with id: " + id));
    }

    /**
     * Create new post
     * Validates that user exists
     */
    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody Post post) {
        logger.info("POST /api/posts - Creating new post by user: {}", post.getUserId());
        
        // Validate user exists
        if (!userRepository.existsById(post.getUserId())) {
            logger.warn("User not found: {}", post.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User not found with id: " + post.getUserId());
        }

        Post savedPost = postRepository.save(post);
        logger.info("Post created successfully with ID: {}", savedPost.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    /**
     * Update existing post
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable UUID id, @RequestBody Post postDetails) {
        logger.info("PUT /api/posts/{} - Updating post", id);
        
        return postRepository.findById(id)
                .map(post -> {
                    if (postDetails.getTitle() != null) {
                        post.setTitle(postDetails.getTitle());
                    }
                    if (postDetails.getContent() != null) {
                        post.setContent(postDetails.getContent());
                    }
                    
                    Post updatedPost = postRepository.save(post);
                    logger.info("Post updated successfully: {}", id);
                    return ResponseEntity.ok(updatedPost);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Post not found with id: " + id));
    }

    /**
     * Delete post
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable UUID id) {
        logger.info("DELETE /api/posts/{} - Deleting post", id);
        
        return postRepository.findById(id)
                .map(post -> {
                    postRepository.delete(post);
                    logger.info("Post deleted successfully: {}", id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
