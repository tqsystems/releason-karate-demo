Feature: User Management API
  Test suite for User CRUD operations

  Background:
    * url 'http://localhost:8080'
    * header Accept = 'application/json'

  Scenario: Get all users
    Given path '/api/users'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0] contains { id: '#uuid', email: '#string', name: '#string', age: '#number' }
    And match response[0].createdAt == '#present'
    And match response[0].updatedAt == '#present'

  Scenario: Get user by ID
    # First get all users to get a valid ID
    Given path '/api/users'
    When method GET
    Then status 200
    And def userId = response[0].id
    
    # Now get specific user
    Given path '/api/users', userId
    When method GET
    Then status 200
    And match response.id == userId
    And match response contains { email: '#string', name: '#string', age: '#number' }

  Scenario: Create valid user
    Given path '/api/users'
    And request { email: 'newuser@example.com', name: 'New User', age: 25 }
    When method POST
    Then status 201
    And match response contains { id: '#uuid', email: 'newuser@example.com', name: 'New User', age: 25 }
    And match response.createdAt == '#present'

  Scenario: Create invalid user - missing email
    Given path '/api/users'
    And request { name: 'Invalid User', age: 25 }
    When method POST
    Then status 400

  Scenario: Create invalid user - invalid email format
    Given path '/api/users'
    And request { email: 'invalid-email', name: 'User', age: 25 }
    When method POST
    Then status 400

  Scenario: Update user
    # First get a user
    Given path '/api/users'
    When method GET
    Then status 200
    And def userId = response[0].id
    
    # Update the user
    Given path '/api/users', userId
    And request { name: 'Updated Name', age: 30 }
    When method PUT
    Then status 200
    And match response.name == 'Updated Name'
    And match response.age == 30

  Scenario: Delete user
    # First create a new user
    Given path '/api/users'
    And request { email: 'todelete@example.com', name: 'To Delete', age: 22 }
    When method POST
    Then status 201
    And def userId = response.id
    
    # Delete the user
    Given path '/api/users', userId
    When method DELETE
    Then status 204
    
    # Verify user is deleted
    Given path '/api/users', userId
    When method GET
    Then status 404
