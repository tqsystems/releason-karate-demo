# Extending the Demo for Your Own API

This guide shows you how to customize this demo repository for your own application.

## Quick Customization Checklist

- [ ] Replace API endpoints with your own
- [ ] Update entity models for your domain
- [ ] Modify Karate tests for your scenarios
- [ ] Adjust coverage thresholds
- [ ] Update documentation
- [ ] Configure Releason webhook

## 1. Replacing API Endpoints

### Step 1: Define Your Domain Models

Replace the User, Post, Comment entities with your own:

```java
// src/main/java/com/example/entity/YourEntity.java
@Entity
@Table(name = "your_table")
public class YourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Add your fields
    private String field1;
    private Integer field2;
    
    // Getters and setters
}
```

### Step 2: Create Repositories

```java
// src/main/java/com/example/repository/YourRepository.java
@Repository
public interface YourRepository extends JpaRepository<YourEntity, UUID> {
    // Add custom queries
    List<YourEntity> findByField1(String field1);
}
```

### Step 3: Build Controllers

```java
// src/main/java/com/example/controller/YourController.java
@RestController
@RequestMapping("/api/your-resource")
public class YourController {
    
    @Autowired
    private YourRepository repository;
    
    @GetMapping
    public ResponseEntity<List<YourEntity>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }
    
    // Add other CRUD endpoints
}
```

### Step 4: Update Data Initializer

```java
// src/main/java/com/example/config/DataInitializer.java
@Override
public void run(String... args) {
    // Create sample data for your entities
    YourEntity entity1 = new YourEntity("value1", 123);
    repository.save(entity1);
}
```

## 2. Creating Karate Tests for Your API

### Step 1: Create Feature File

```gherkin
# src/test/resources/features/your-resource.feature
Feature: Your Resource API
  Test suite for Your Resource CRUD operations

  Background:
    * url 'http://localhost:8080'
    * header Accept = 'application/json'

  Scenario: Get all resources
    Given path '/api/your-resource'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0] contains { id: '#uuid', field1: '#string' }

  Scenario: Create new resource
    Given path '/api/your-resource'
    And request { field1: 'test', field2: 123 }
    When method POST
    Then status 201
    And match response contains { id: '#uuid', field1: 'test' }

  # Add validation tests (intentionally failing tests for demo)
  Scenario: Create invalid resource - missing required field
    Given path '/api/your-resource'
    And request { field2: 123 }
    When method POST
    Then status 400
```

### Step 2: Update Test Runner

```java
// src/test/java/com/example/KarateTest.java
@Karate.Test
Karate testYourResource() {
    return Karate.run("classpath:features/your-resource.feature")
            .relativeTo(getClass());
}
```

### Step 3: Run Tests

```bash
mvn clean test
```

## 3. Adjusting Coverage Thresholds

### In pom.xml

Change JaCoCo coverage requirements:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.90</minimum>  <!-- Change from 0.75 to 0.90 -->
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.85</minimum>  <!-- Add branch coverage -->
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

### Update GitHub Actions

Adjust thresholds in `.github/workflows/karate-test.yml`:

```yaml
- name: Check coverage threshold
  run: |
    COVERAGE=${{ steps.coverage.outputs.COVERAGE }}
    THRESHOLD=90.0
    
    if (( $(echo "$COVERAGE < $THRESHOLD" | bc -l) )); then
      echo "❌ Coverage $COVERAGE% is below threshold $THRESHOLD%"
      exit 1
    fi
```

## 4. Customizing Releason Integration

### Adjust Confidence Formula Weights

In Releason dashboard settings, customize the formula:

```
Default:
Score = (Coverage × 0.6) + (Pass Rate × 0.3) + (Risk × 0.1)

For test-heavy projects:
Score = (Coverage × 0.4) + (Pass Rate × 0.5) + (Risk × 0.1)

For coverage-focused projects:
Score = (Coverage × 0.7) + (Pass Rate × 0.2) + (Risk × 0.1)
```

### Add Custom Metrics

Extend the webhook payload:

```yaml
- name: Send metrics to Releason
  run: |
    # Extract additional metrics
    COMPLEXITY=$(grep -r "TODO" src/ | wc -l)
    SECURITY_ISSUES=$(grep -r "FIXME" src/ | wc -l)
    
    curl -X POST "${{ secrets.RELEASON_WEBHOOK_URL }}" \
      -H "Content-Type: application/json" \
      -d '{
        ...existing fields...,
        "custom_metrics": {
          "code_complexity": '$COMPLEXITY',
          "security_issues": '$SECURITY_ISSUES',
          "tech_debt": '$TECH_DEBT'
        }
      }'
```

## 5. Integrating with Existing Codebase

### Option A: Add to Existing Maven Project

1. Copy `src/test/resources/features/` to your project
2. Add Karate dependency to your `pom.xml`
3. Copy JaCoCo plugin configuration
4. Create KarateTest.java runner

### Option B: Separate Testing Repository

1. Create new repository for tests
2. Point tests to your API URL
3. Run tests independently
4. Send metrics to Releason

### Option C: Monorepo Approach

```
your-project/
├── api/                    # Your API code
├── frontend/               # Your frontend
└── tests/                  # This demo (adapted)
    ├── karate/
    └── performance/
```

## 6. Adding More Test Scenarios

### Testing Authentication

```gherkin
Feature: Authentication

  Background:
    * url 'http://localhost:8080'
    * def loginPayload = { username: 'test', password: 'test123' }

  Scenario: Login successfully
    Given path '/api/auth/login'
    And request loginPayload
    When method POST
    Then status 200
    And match response contains { token: '#string' }
    And def authToken = response.token

  Scenario: Access protected endpoint
    # First login
    Given path '/api/auth/login'
    And request loginPayload
    When method POST
    Then status 200
    And def authToken = response.token
    
    # Now access protected resource
    Given path '/api/protected'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
```

### Testing Error Scenarios

```gherkin
  Scenario: Handle server error gracefully
    Given path '/api/simulate-error'
    When method GET
    Then status 500
    And match response contains { error: '#string', message: '#string' }

  Scenario: Handle not found
    Given path '/api/users/00000000-0000-0000-0000-000000000000'
    When method GET
    Then status 404
```

### Performance Testing

```gherkin
  Scenario: Load test - 100 concurrent requests
    * def sleep = function(ms){ java.lang.Thread.sleep(ms) }
    * def results = []
    
    * def callApi =
    """
    function() {
      var result = karate.call('classpath:features/users.feature@GetAllUsers');
      return result.responseTime;
    }
    """
    
    # Make 100 calls
    * eval for(var i = 0; i < 100; i++) results.push(callApi())
    
    # Calculate average
    * def avgTime = results.reduce((a,b) => a + b, 0) / results.length
    * print 'Average response time:', avgTime, 'ms'
    * assert avgTime < 500
```

## 7. Changing Database

### From H2 to PostgreSQL

1. Update `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

3. Update `docker-compose.yml`:

```yaml
services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: yourdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
```

### From H2 to MySQL

Similar process, use MySQL driver and dialect.

## 8. Adding More Integrations

### Slack Notifications

Add to GitHub Actions workflow:

```yaml
- name: Notify Slack
  if: failure()
  run: |
    curl -X POST ${{ secrets.SLACK_WEBHOOK }} \
      -H 'Content-Type: application/json' \
      -d '{
        "text": "❌ Tests failed in ${{ github.repository }}",
        "blocks": [{
          "type": "section",
          "text": {
            "type": "mrkdwn",
            "text": "*Build Failed*\nBranch: ${{ github.ref_name }}\nCommit: ${{ github.sha }}"
          }
        }]
      }'
```

### Jira Integration

```yaml
- name: Create Jira ticket for failures
  if: steps.tests.outputs.FAILED > 0
  run: |
    curl -X POST https://your-domain.atlassian.net/rest/api/3/issue \
      -H "Authorization: Basic ${{ secrets.JIRA_TOKEN }}" \
      -H "Content-Type: application/json" \
      -d '{
        "fields": {
          "project": { "key": "PROJ" },
          "summary": "Test failures in build #${{ github.run_number }}",
          "description": "${{ steps.tests.outputs.FAILED }} tests failed",
          "issuetype": { "name": "Bug" }
        }
      }'
```

## 9. Best Practices

### Test Organization

```
features/
├── common/
│   ├── auth.feature          # Reusable auth scenarios
│   └── helpers.feature        # Shared functions
├── smoke/
│   ├── health-check.feature
│   └── critical-paths.feature
├── api/
│   ├── users.feature
│   ├── posts.feature
│   └── comments.feature
└── integration/
    └── end-to-end.feature
```

### Code Coverage Strategy

1. **Critical Paths:** Aim for 95%+ coverage
2. **Business Logic:** 90%+ coverage
3. **Simple CRUD:** 80%+ coverage
4. **Configuration:** Can be lower

### Testing Pyramid

- **Unit Tests:** 70% (not covered in this demo)
- **Integration Tests:** 20% (Karate tests)
- **E2E Tests:** 10% (Karate + UI tests)

## 10. Common Customizations

### Add Request Logging

```java
// src/main/java/com/example/config/LoggingFilter.java
@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        logger.info("Request: {} {}", req.getMethod(), req.getRequestURI());
        chain.doFilter(request, response);
    }
}
```

### Add API Versioning

```java
@RestController
@RequestMapping("/api/v1/users")  // Add version prefix
public class UserController {
    // ...
}
```

### Add Rate Limiting

Use libraries like Bucket4j or Spring Cloud Gateway.

## Need Help?

- **Examples:** See `src/` folder for working examples
- **Karate Docs:** [karate.io](https://karatelabs.github.io/karate/)
- **Spring Boot Docs:** [spring.io](https://spring.io/guides)
- **Releason Support:** support@releason.com
