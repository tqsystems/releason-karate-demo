# Releason + Karate Integration Demo

A complete, production-quality example showing how to:
- Build API tests with Karate framework
- Capture code coverage with JaCoCo
- Send metrics to Releason dashboard
- Make data-driven deployment decisions

## Quick Start (5 minutes)

### Prerequisites
- Docker & Docker Compose
- Java 17+
- Maven 3.9+
- Git

### Setup
```bash
git clone https://github.com/tqsystems/releason-karate-demo
cd releason-karate-demo
cp .env.example .env  # Edit with your Releason webhook URL (optional)
docker-compose up -d
mvn clean test
```

### View Results
- **Terminal:** See test results and coverage
- **Releason Dashboard:** https://releason.com/dashboard

## What This Demo Shows

### Real Data
- 14+ Karate test scenarios covering Users, Posts, and Comments APIs
- 80-90% code coverage via JaCoCo
- Some tests intentionally fail (2 out of ~14) to demonstrate risk detection
- Real business logic with realistic validation

### Real Metrics
- **Test Pass Rate:** 75-85%
- **Code Coverage:** 80-90%
- **Release Confidence:** 86% (calculated by Releason)
- **Risk Level:** Medium (failing tests detected)

### Real Integration
- GitHub Actions workflow automatically runs on push
- Webhook sends metrics to Releason platform
- Dashboard displays live metrics and insights
- Exactly how it works in production environments

## What You'll See on Dashboard

When you integrate with Releason, you'll see:

✅ **Release Confidence Score:** 86%  
✅ **Risk Level:** Medium (failing tests detected)  
✅ **Test Coverage:** 87% (285 / 300 lines)  
✅ **Test Pass Rate:** 75% (10 passed, 2 failed)  

### Risk Items Detected:
- 2 tests failing (POST /users email validation)
- Coverage below 90% threshold

### Recommendation:
> "Fix failing tests before deployment. Estimated time to ship: 1.5h"

## Project Structure

```
releason-karate-demo/
├── src/
│   ├── main/java/com/example/
│   │   ├── Application.java              # Spring Boot main class
│   │   ├── controller/                   # REST controllers
│   │   ├── entity/                       # JPA entities
│   │   ├── repository/                   # Data repositories
│   │   └── config/                       # Configuration classes
│   └── test/
│       ├── java/com/example/
│       │   └── KarateTest.java           # Karate test runner
│       └── resources/features/
│           ├── users.feature             # User API tests
│           ├── posts.feature             # Post API tests
│           └── comments.feature          # Comment API tests
├── pom.xml                               # Maven configuration
├── docker-compose.yml                    # Docker setup
├── Dockerfile                            # Container image
└── .github/workflows/
    └── karate-test.yml                   # CI/CD pipeline
```

## API Endpoints

The demo includes a complete REST API with the following endpoints:

### Users API
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Posts API
- `GET /api/posts` - List all posts
- `GET /api/posts/{id}` - Get post by ID
- `GET /api/posts?userId={id}` - Get user's posts
- `POST /api/posts` - Create new post
- `PUT /api/posts/{id}` - Update post

### Comments API
- `GET /api/comments` - List all comments
- `GET /api/comments/{id}` - Get comment by ID
- `GET /api/comments?postId={id}` - Get post's comments
- `POST /api/comments` - Create new comment
- `DELETE /api/comments/{id}` - Delete comment

## Running the Demo

### Local Development
```bash
# Start the API server
mvn spring-boot:run

# In another terminal, run tests
mvn test

# View coverage report
open target/site/jacoco/index.html
```

### Docker
```bash
# Start services
docker-compose up -d

# Check health
curl http://localhost:8080/health

# Run tests
mvn clean test

# Stop services
docker-compose down
```

### CI/CD (GitHub Actions)
1. Push code to GitHub
2. Workflow runs automatically
3. Tests execute with coverage
4. Metrics sent to Releason
5. Dashboard updates in real-time

## How to Customize

See [EXTENDING.md](EXTENDING.md) for:
- Adding new API endpoints
- Creating new test scenarios
- Changing coverage targets
- Integrating with your own API

## Troubleshooting

See [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) for:
- Docker issues
- Test failures
- Coverage problems
- Webhook connectivity

## Next Steps

1. ✅ Run this demo locally
2. ✅ See Releason dashboard populated (requires setup)
3. ✅ Read [RELEASON_INTEGRATION.md](RELEASON_INTEGRATION.md)
4. ✅ Clone this structure into your codebase
5. ✅ Customize for your API
6. ✅ Deploy to production CI/CD

## Documentation

- [README.md](README.md) (this file) - Quick start guide
- [SETUP.md](SETUP.md) - Detailed installation instructions
- [RELEASON_INTEGRATION.md](RELEASON_INTEGRATION.md) - How to connect to Releason
- [DASHBOARD_WALKTHROUGH.md](DASHBOARD_WALKTHROUGH.md) - What to expect on dashboard
- [EXTENDING.md](EXTENDING.md) - Customization guide
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) - Technical details
- [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) - Common issues

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.2** - Application framework
- **H2 Database** - In-memory database
- **Karate 1.4** - API testing framework
- **JaCoCo 0.8** - Code coverage tool
- **Maven 3.9** - Build tool
- **Docker** - Containerization
- **GitHub Actions** - CI/CD pipeline

## License

MIT License - Use freely in your organization

## Support

- **Documentation:** See [docs/](docs/) folder
- **Issues:** [GitHub Issues](https://github.com/tqsystems/releason-karate-demo/issues)
- **Questions:** Contact Releason Support

---

**Made with ❤️ by TQ Systems**  
Try Releason at [https://releason.com](https://releason.com)