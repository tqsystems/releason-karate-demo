package com.example.config;

import com.example.entity.Comment;
import com.example.entity.Post;
import com.example.entity.User;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes the database with sample data on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void run(String... args) {
        logger.info("Initializing database with sample data...");

        // Create sample users
        List<User> users = new ArrayList<>();
        users.add(new User("john.doe@example.com", "John Doe", 28));
        users.add(new User("jane.smith@example.com", "Jane Smith", 32));
        users.add(new User("bob.johnson@example.com", "Bob Johnson", 45));
        users.add(new User("alice.brown@example.com", "Alice Brown", 24));
        users.add(new User("charlie.wilson@example.com", "Charlie Wilson", 36));

        users = userRepository.saveAll(users);
        logger.info("Created {} users", users.size());

        // Create sample posts
        List<Post> posts = new ArrayList<>();
        posts.add(new Post("Getting Started with Spring Boot", 
                "Spring Boot makes it easy to create stand-alone, production-grade Spring applications...", 
                users.get(0).getId()));
        posts.add(new Post("Introduction to Karate Framework", 
                "Karate is an open-source tool to combine API test-automation, mocks, and performance-testing...", 
                users.get(0).getId()));
        posts.add(new Post("Microservices Architecture Patterns", 
                "Microservices architecture is a method of developing software applications as independently deployable services...", 
                users.get(1).getId()));
        posts.add(new Post("Docker Best Practices", 
                "Docker has revolutionized how we build, ship, and run applications...", 
                users.get(1).getId()));
        posts.add(new Post("REST API Design Principles", 
                "RESTful APIs are everywhere, and understanding how to design them properly is crucial...", 
                users.get(2).getId()));
        posts.add(new Post("CI/CD Pipeline Setup", 
                "Continuous Integration and Continuous Deployment are essential practices in modern software development...", 
                users.get(3).getId()));
        posts.add(new Post("Database Optimization Techniques", 
                "Database performance is critical for application speed and user experience...", 
                users.get(3).getId()));
        posts.add(new Post("Security Best Practices for Web Applications", 
                "Security should be a top priority when developing web applications...", 
                users.get(4).getId()));

        posts = postRepository.saveAll(posts);
        logger.info("Created {} posts", posts.size());

        // Create sample comments
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Great introduction! Very helpful for beginners.", 
                posts.get(0).getId(), users.get(1).getId()));
        comments.add(new Comment("Thanks for sharing this. I learned a lot.", 
                posts.get(0).getId(), users.get(2).getId()));
        comments.add(new Comment("Can you provide more examples?", 
                posts.get(1).getId(), users.get(3).getId()));
        comments.add(new Comment("This is exactly what I was looking for!", 
                posts.get(1).getId(), users.get(4).getId()));
        comments.add(new Comment("Excellent explanation of microservices.", 
                posts.get(2).getId(), users.get(0).getId()));
        comments.add(new Comment("How does this scale in production?", 
                posts.get(2).getId(), users.get(4).getId()));
        comments.add(new Comment("Docker has indeed changed everything!", 
                posts.get(3).getId(), users.get(2).getId()));
        comments.add(new Comment("Very comprehensive guide on REST APIs.", 
                posts.get(4).getId(), users.get(1).getId()));
        comments.add(new Comment("CI/CD is a game changer for our team.", 
                posts.get(5).getId(), users.get(0).getId()));
        comments.add(new Comment("These optimization tips are gold!", 
                posts.get(6).getId(), users.get(2).getId()));
        comments.add(new Comment("Security should always come first.", 
                posts.get(7).getId(), users.get(1).getId()));
        comments.add(new Comment("Looking forward to more content like this!", 
                posts.get(7).getId(), users.get(3).getId()));

        comments = commentRepository.saveAll(comments);
        logger.info("Created {} comments", comments.size());

        logger.info("Database initialized with {} users, {} posts, {} comments", 
                users.size(), posts.size(), comments.size());
    }
}
