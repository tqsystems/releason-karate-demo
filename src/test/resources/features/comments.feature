Feature: Comment Management API
  Test suite for Comment CRUD operations

  Background:
    * url 'http://localhost:8080'
    * header Accept = 'application/json'

  Scenario: Get all comments
    Given path '/api/comments'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0] contains { id: '#uuid', content: '#string', postId: '#uuid', userId: '#uuid' }
    And match response[0].createdAt == '#present'

  Scenario: Get comment by ID
    # First get all comments to get a valid ID
    Given path '/api/comments'
    When method GET
    Then status 200
    And def commentId = response[0].id
    
    # Now get specific comment
    Given path '/api/comments', commentId
    When method GET
    Then status 200
    And match response.id == commentId
    And match response contains { content: '#string', postId: '#uuid', userId: '#uuid' }

  Scenario: Create comment on post
    # First get a post and user
    Given path '/api/posts'
    When method GET
    Then status 200
    And def postId = response[0].id
    
    Given path '/api/users'
    When method GET
    Then status 200
    And def userId = response[0].id
    
    # Create comment
    Given path '/api/comments'
    And request { content: 'This is a great post!', postId: '#(postId)', userId: '#(userId)' }
    When method POST
    Then status 201
    And match response contains { id: '#uuid', content: 'This is a great post!', postId: '#(postId)', userId: '#(userId)' }

  Scenario: Delete comment
    # First create a comment
    Given path '/api/posts'
    When method GET
    Then status 200
    And def postId = response[0].id
    
    Given path '/api/users'
    When method GET
    Then status 200
    And def userId = response[0].id
    
    Given path '/api/comments'
    And request { content: 'Comment to delete', postId: '#(postId)', userId: '#(userId)' }
    When method POST
    Then status 201
    And def commentId = response.id
    
    # Delete the comment
    Given path '/api/comments', commentId
    When method DELETE
    Then status 204
    
    # Verify comment is deleted
    Given path '/api/comments', commentId
    When method GET
    Then status 404
