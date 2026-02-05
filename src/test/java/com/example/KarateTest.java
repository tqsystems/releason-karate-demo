package com.example;

import com.intuit.karate.junit5.Karate;

/**
 * Karate Test Runner
 * Executes all feature files in the features directory
 */
public class KarateTest {

    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:features").relativeTo(getClass());
    }

    @Karate.Test
    Karate testUsers() {
        return Karate.run("classpath:features/users.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate testPosts() {
        return Karate.run("classpath:features/posts.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate testComments() {
        return Karate.run("classpath:features/comments.feature").relativeTo(getClass());
    }
}
