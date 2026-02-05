# Architecture Documentation

Technical overview of the Releason + Karate demo architecture.

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         GitHub Repository                        │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Source Code (Java, Spring Boot)                          │  │
│  │  Test Suite (Karate Features)                             │  │
│  │  CI/CD Workflow (GitHub Actions)                          │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ git push
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       GitHub Actions Runner                      │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  1. Build Spring Boot Application                         │  │
│  │  2. Start Docker Container                                │  │
│  │  3. Run Karate Tests                                      │  │
│  │  4. Generate JaCoCo Coverage                              │  │
│  │  5. Extract Metrics                                       │  │
│  │  6. Send to Releason                                       │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP POST
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Releason Platform                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Webhook Receiver                                         │  │
│  │  Metrics Processor                                        │  │
│  │  Confidence Calculator                                    │  │
│  │  Dashboard & Analytics                                    │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Component Architecture

### 1. Spring Boot Application

```
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Application                     │
│                                                                  │
│  ┌────────────────┐      ┌────────────────┐                    │
│  │  Controllers   │─────▶│  Repositories  │                    │
│  │  - User        │      │  - User        │                    │
│  │  - Post        │      │  - Post        │                    │
│  │  - Comment     │      │  - Comment     │                    │
│  └────────────────┘      └────────────────┘                    │
│         │                         │                              │
│         │                         │                              │
│         ▼                         ▼                              │
│  ┌────────────────┐      ┌────────────────┐                    │
│  │   Entities     │      │   H2 Database  │                    │
│  │  - User        │◀─────│  (In-Memory)   │                    │
│  │  - Post        │      │                │                    │
│  │  - Comment     │      └────────────────┘                    │
│  └────────────────┘                                             │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Data Initializer (on startup)                           │  │
│  │  - 5 Users, 8 Posts, 12 Comments                         │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

#### Technology Stack

- **Framework:** Spring Boot 3.2.1
- **Language:** Java 17
- **Database:** H2 (in-memory)
- **ORM:** Spring Data JPA / Hibernate
- **Validation:** Jakarta Validation API
- **Build Tool:** Maven 3.9

#### REST API Design

**Endpoint Pattern:** `/api/{resource}[/{id}]`

**HTTP Methods:**
- `GET` - Retrieve resources
- `POST` - Create new resource
- `PUT` - Update existing resource
- `DELETE` - Remove resource

**Response Codes:**
- `200 OK` - Successful GET/PUT
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation error
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

### 2. Test Layer (Karate)

```
┌─────────────────────────────────────────────────────────────────┐
│                         Test Suite                               │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  KarateTest.java                                           │ │
│  │  - JUnit 5 Test Runner                                     │ │
│  │  - Discovers all .feature files                            │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              │                                   │
│                              ▼                                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Feature Files                                             │ │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐      │ │
│  │  │ users.feature│ │ posts.feature│ │comments.feat.│      │ │
│  │  │ 7 scenarios  │ │ 5 scenarios  │ │ 4 scenarios  │      │ │
│  │  └──────────────┘ └──────────────┘ └──────────────┘      │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              │                                   │
│                              ▼                                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Test Results                                              │ │
│  │  - JUnit XML reports                                       │ │
│  │  - Karate JSON reports                                     │ │
│  │  - Test execution logs                                     │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

#### Test Scenarios

| Feature | Scenarios | Passing | Failing | Purpose |
|---------|-----------|---------|---------|---------|
| users.feature | 7 | 5 | 2 | Test user CRUD + validation |
| posts.feature | 5 | 5 | 0 | Test post CRUD |
| comments.feature | 4 | 4 | 0 | Test comment CRUD |
| **Total** | **16** | **14** | **2** | **~87% pass rate** |

**Intentionally Failing Tests:**
1. `Create invalid user - missing email` - Demonstrates validation
2. `Create invalid user - invalid email format` - Demonstrates validation

### 3. Coverage Layer (JaCoCo)

```
┌─────────────────────────────────────────────────────────────────┐
│                      JaCoCo Coverage                             │
│                                                                  │
│  ┌────────────────┐                                             │
│  │  JaCoCo Agent  │                                             │
│  │  (Runtime)     │                                             │
│  └────────────────┘                                             │
│         │                                                        │
│         │ Instruments bytecode                                  │
│         ▼                                                        │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Application Under Test                                    │ │
│  │  - Records line coverage                                   │ │
│  │  - Records branch coverage                                 │ │
│  │  - Records method coverage                                 │ │
│  └────────────────────────────────────────────────────────────┘ │
│         │                                                        │
│         │ After test execution                                  │
│         ▼                                                        │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Coverage Reports                                          │ │
│  │  - jacoco.xml (for CI/CD)                                  │ │
│  │  - jacoco.csv (for analysis)                               │ │
│  │  - HTML report (for developers)                            │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

#### Coverage Metrics

| Metric | Target | Demo | Status |
|--------|--------|------|--------|
| Line Coverage | 90% | 85% | ⚠️ Below target |
| Branch Coverage | 85% | 83% | ⚠️ Below target |
| Method Coverage | 90% | 88% | ⚠️ Below target |

**Excluded from Coverage:**
- Entity classes (auto-generated getters/setters)
- Repository interfaces (Spring Data generates implementations)
- Configuration classes
- Main application class

### 4. CI/CD Pipeline

```
┌─────────────────────────────────────────────────────────────────┐
│                    GitHub Actions Workflow                       │
│                                                                  │
│  1. [Checkout Code]                                             │
│           │                                                      │
│           ▼                                                      │
│  2. [Setup Java 17]                                             │
│           │                                                      │
│           ▼                                                      │
│  3. [Start Docker Services]                                     │
│           │                                                      │
│           ▼                                                      │
│  4. [Wait for API Health Check] ←── Retry up to 60 times       │
│           │                                                      │
│           ▼                                                      │
│  5. [Run Maven Tests + JaCoCo]                                  │
│           │                                                      │
│           ├──▶ [Extract Test Metrics]                           │
│           │         • PASSED, FAILED, TOTAL                     │
│           │                                                      │
│           └──▶ [Extract Coverage Metrics]                       │
│                   • COVERAGE, BRANCHES, FUNCTIONS               │
│           │                                                      │
│           ▼                                                      │
│  6. [Send to Releason] ────────────────────────▶ Webhook         │
│           │                                                      │
│           ▼                                                      │
│  7. [Comment on PR] (if applicable)                             │
│           │                                                      │
│           ▼                                                      │
│  8. [Upload Artifacts]                                          │
│           │                                                      │
│           ▼                                                      │
│  9. [Cleanup Docker]                                            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### Workflow Triggers

- **Push:** Any push to `main` or `develop` branch
- **Pull Request:** Any PR targeting `main` or `develop`
- **Manual:** Can be triggered manually from Actions tab

#### Workflow Duration

- **Average:** 3-5 minutes
- **Breakdown:**
  - Checkout & Setup: 30s
  - Docker Build & Start: 60-90s
  - Test Execution: 60-120s
  - Reporting & Cleanup: 30s

### 5. Docker Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      Docker Compose                              │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Service: api                                              │ │
│  │  - Image: Built from Dockerfile                            │ │
│  │  - Port: 8080:8080                                         │ │
│  │  - Health Check: /health endpoint                          │ │
│  │  - Network: releason-demo                                   │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                  │
│  Network: releason-demo (bridge)                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Multi-Stage Docker Build

**Stage 1: Builder**
- Base: `maven:3.9-eclipse-temurin-17-alpine`
- Downloads dependencies
- Compiles source code
- Packages JAR file

**Stage 2: Runtime**
- Base: `eclipse-temurin:17-jre-alpine`
- Copies JAR from builder
- Minimal image size (~200MB)
- Only includes runtime dependencies

### 6. Data Flow

```
Developer Push
      │
      ▼
GitHub Actions Triggered
      │
      ├──▶ Build Application
      │         │
      │         ▼
      │    Start Docker Container
      │         │
      │         ▼
      │    Run Karate Tests ──▶ Test Results
      │         │
      │         ▼
      │    Generate JaCoCo ───▶ Coverage Data
      │
      ▼
Extract Metrics
      │
      ├──▶ Test Metrics (passed, failed, total)
      ├──▶ Coverage Metrics (lines, branches, functions)
      └──▶ Metadata (repo, branch, commit, timestamp)
      │
      ▼
Send to Releason Webhook
      │
      ▼
Releason Processes
      │
      ├──▶ Calculate Confidence Score
      ├──▶ Identify Risk Items
      ├──▶ Generate Recommendations
      └──▶ Update Dashboard
      │
      ▼
Developer Views Dashboard
```

## Security Considerations

### Authentication
- **GitHub Secrets:** Webhook URL stored securely
- **API:** No authentication in demo (add for production)
- **Database:** In-memory, no persistent storage

### Data Privacy
- **Metrics Only:** Only test/coverage metrics sent to Releason
- **No Source Code:** Source code never leaves your infrastructure
- **No Secrets:** Environment variables and secrets not transmitted

### Best Practices
1. Always use HTTPS for webhooks
2. Validate webhook payloads
3. Use GitHub Secrets for sensitive data
4. Implement rate limiting in production
5. Add authentication to API endpoints

## Performance Characteristics

### Application Startup
- **Cold Start:** 8-12 seconds
- **With Data Init:** +2-3 seconds
- **Total:** ~10-15 seconds

### Test Execution
- **Per Scenario:** 50-200ms average
- **Total Suite:** 2-3 minutes
- **With Coverage:** +30-60 seconds

### Resource Usage
- **Memory:** ~512MB (Docker container)
- **CPU:** 1 core (sufficient)
- **Disk:** ~200MB (container image)

## Scalability

### Horizontal Scaling
- Stateless API design allows multiple instances
- H2 database should be replaced with PostgreSQL/MySQL
- Load balancer needed for production

### Vertical Scaling
- Current setup handles 100+ concurrent requests
- For higher load, increase container resources
- Consider caching layer (Redis)

## Monitoring & Observability

### Application Logs
- **Level:** INFO (configurable)
- **Format:** Structured logging with timestamps
- **Output:** Console (captured by Docker)

### Health Checks
- **Endpoint:** `/health`
- **Response:** `{"status": "UP", "timestamp": "..."}`
- **Used By:** Docker, Kubernetes, monitoring tools

### Metrics Available
- Test results (passed, failed, total)
- Code coverage (lines, branches, functions)
- Test execution time
- Build duration

## Future Enhancements

### Planned Improvements
1. Add performance testing with Gatling
2. Integrate security scanning (OWASP Dependency Check)
3. Add mutation testing (PIT)
4. Implement load testing scenarios
5. Add API documentation (Swagger/OpenAPI)
6. Add contract testing (Pact)

### Production Readiness Checklist
- [ ] Replace H2 with production database
- [ ] Add authentication/authorization
- [ ] Implement rate limiting
- [ ] Add request/response logging
- [ ] Set up centralized logging (ELK, Splunk)
- [ ] Configure monitoring (Prometheus, Grafana)
- [ ] Add distributed tracing (Jaeger, Zipkin)
- [ ] Implement circuit breakers (Resilience4j)
- [ ] Add caching layer (Redis)
- [ ] Configure SSL/TLS

## References

- **Spring Boot:** [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
- **Karate:** [https://karatelabs.github.io/karate/](https://karatelabs.github.io/karate/)
- **JaCoCo:** [https://www.jacoco.org/](https://www.jacoco.org/)
- **Docker:** [https://docs.docker.com/](https://docs.docker.com/)
- **GitHub Actions:** [https://docs.github.com/actions](https://docs.github.com/actions)
- **Releason:** [https://releason.com/docs](https://releason.com/docs)
