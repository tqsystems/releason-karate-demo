Feature: Post Management API
  Test suite for Post CRUD operations

  Background:
    * url 'http://localhost:8080'
    * header Accept = 'application/json'
    
  Scenario: Get all posts
    Given path '/api/posts'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0] contains { id: '#uuid', title: '#string', content: '#string', userId: '#uuid' }
    And match response[0].createdAt == '#present'

  Scenario: Get post by ID
    # First get all posts to get a valid ID
    Given path '/api/posts'
    When method GET
    Then status 200
    And def postId = response[0].id
    
    # Now get specific post
    Given path '/api/posts', postId
    When method GET
    Then status 200
    And match response.id == postId
    And match response contains { title: '#string', content: '#string', userId: '#uuid' }

  Scenario: Get posts by user
    # First get a user
    Given path '/api/users'
    When method GET
    Then status 200
    And def userId = response[0].id
    
    # Get user's posts
    Given path '/api/posts'
    And param userId = userId
    When method GET
    Then status 200
    And match response == '#array'

  Scenario: Create new post
    # First get a user
    Given path '/api/users'
    When method GET
    Then status 200
    And def userId = response[0].id
    
    # Create post
    Given path '/api/posts'
    And request { title: 'New Post', content: 'This is a new post content...', userId: '#(userId)' }
    When method POST
    Then status 201
    And match response contains { id: '#uuid', title: 'New Post', userId: '#(userId)' }

  Scenario: Update post
    # First get a post
    Given path '/api/posts'
    When method GET
    Then status 200
    And def postId = response[0].id
    
    # Update the post
    Given path '/api/posts', postId
    And request { title: 'Updated Title', content: 'Updated content' }
    When method PUT
    Then status 200
    And match response.title == 'Updated Title'
    And match response.content == 'Updated content'
