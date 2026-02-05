# Troubleshooting Guide

Common issues and solutions for the Releason + Karate demo.

## Table of Contents

1. [Docker Issues](#docker-issues)
2. [Maven/Build Issues](#mavenbuild-issues)
3. [Test Execution Issues](#test-execution-issues)
4. [Coverage Issues](#coverage-issues)
5. [Webhook/Releason Issues](#webhookunis-issues)
6. [GitHub Actions Issues](#github-actions-issues)
7. [Performance Issues](#performance-issues)

---

## Docker Issues

### Issue: "docker: command not found"

**Symptom:** Command line shows `docker: command not found`

**Cause:** Docker not installed or not in PATH

**Solution:**
```bash
# Check if Docker is installed
which docker

# macOS: Install Docker Desktop
brew install --cask docker

# Linux: Install Docker Engine
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Verify installation
docker --version
```

### Issue: "Cannot connect to the Docker daemon"

**Symptom:** `Error: Cannot connect to the Docker daemon at unix:///var/run/docker.sock`

**Cause:** Docker daemon not running

**Solution:**
```bash
# macOS: Start Docker Desktop application
open -a Docker

# Linux: Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Verify Docker is running
docker ps
```

### Issue: "Port 8080 already in use"

**Symptom:** `Error: Bind for 0.0.0.0:8080 failed: port is already allocated`

**Cause:** Another service using port 8080

**Solution:**
```bash
# Find what's using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process (use PID from above)
kill -9 <PID>

# Or change port in docker-compose.yml
ports:
  - "8081:8080"  # Use 8081 instead
```

### Issue: "Container exits immediately"

**Symptom:** Container starts but exits with code 1 or 137

**Cause:** Application error or out of memory

**Solution:**
```bash
# Check logs
docker-compose logs api

# Common errors to look for:
# - "OutOfMemoryError" → Increase container memory
# - "Port already in use" → Change port
# - "Connection refused" → Check database connection

# Increase memory limit
docker-compose.yml:
  api:
    deploy:
      resources:
        limits:
          memory: 1G

# Restart with fresh state
docker-compose down -v
docker-compose up -d
```

### Issue: "Docker build fails"

**Symptom:** `ERROR [builder X/Y] RUN mvn clean package`

**Cause:** Maven build failure in Docker

**Solution:**
```bash
# Test build locally first
mvn clean package

# If local build works, try Docker with no cache
docker-compose build --no-cache

# Check Dockerfile for errors
# Ensure pom.xml and src/ are being copied correctly
```

---

## Maven/Build Issues

### Issue: "mvn: command not found"

**Symptom:** Command line shows `mvn: command not found`

**Cause:** Maven not installed or not in PATH

**Solution:**
```bash
# macOS: Install Maven
brew install maven

# Linux: Install Maven
sudo apt update
sudo apt install maven

# Or download manually
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar xzvf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven

# Add to PATH
echo 'export PATH=/opt/maven/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Verify
mvn -version
```

### Issue: "Failed to execute goal... compilation failure"

**Symptom:** Maven shows compilation errors

**Cause:** Java version mismatch or missing dependencies

**Solution:**
```bash
# Check Java version (must be 17+)
java -version

# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)  # macOS
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64  # Linux

# Clean and rebuild
mvn clean install

# Force dependency update
mvn clean install -U
```

### Issue: "Dependencies cannot be resolved"

**Symptom:** `Could not resolve dependencies for project...`

**Cause:** Network issues or corrupted Maven cache

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Try again with update
mvn clean install -U

# Check internet connectivity
curl https://repo.maven.apache.org/maven2/

# Use different Maven repository (if needed)
# Edit ~/.m2/settings.xml
```

### Issue: "Tests are skipped"

**Symptom:** Maven shows `Tests: 0, Skipped: 0`

**Cause:** Test files not found or misconfigured

**Solution:**
```bash
# Verify test files exist
ls -la src/test/resources/features/

# Check test runner exists
ls -la src/test/java/com/example/KarateTest.java

# Run tests explicitly
mvn test

# Run specific test
mvn test -Dtest=KarateTest
```

---

## Test Execution Issues

### Issue: "Connection refused" during tests

**Symptom:** Tests fail with `Connection refused: localhost:8080`

**Cause:** API not started or not ready

**Solution:**
```bash
# Check API is running
curl http://localhost:8080/health

# If not running, start Docker
docker-compose up -d

# Wait longer for API to start
sleep 15
curl http://localhost:8080/health

# Check API logs
docker-compose logs -f api
```

### Issue: "Tests fail randomly"

**Symptom:** Tests pass sometimes, fail other times

**Cause:** Race conditions, timing issues, or flaky tests

**Solution:**
```bash
# Add delays in Karate tests
* def sleep = function(ms){ java.lang.Thread.sleep(ms) }
* call sleep 1000

# Increase timeout in tests
* configure connectTimeout = 10000
* configure readTimeout = 10000

# Check for data dependencies
# Ensure tests don't depend on specific data order
```

### Issue: "Test fails: Expected 400, got 500"

**Symptom:** Validation tests fail with wrong status code

**Cause:** API returning server error instead of validation error

**Solution:**
```bash
# Check API logs for stack trace
docker-compose logs api | grep -A 20 "Exception"

# Common causes:
# - Missing @Valid annotation on controller
# - Exception handler not configured
# - Validation dependency missing

# Fix in controller:
@PostMapping
public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
    // ...
}
```

### Issue: "JSON parsing errors"

**Symptom:** `Failed to parse JSON response`

**Cause:** API returning HTML or malformed JSON

**Solution:**
```bash
# Check actual response
curl -v http://localhost:8080/api/users

# Look for:
# - Content-Type header (should be application/json)
# - HTML error pages
# - Stack traces

# Add Accept header in Karate
* header Accept = 'application/json'
```

---

## Coverage Issues

### Issue: "Coverage report not generated"

**Symptom:** `target/site/jacoco/` directory doesn't exist

**Cause:** JaCoCo plugin not executed

**Solution:**
```bash
# Ensure JaCoCo plugin is in pom.xml
# Run tests with coverage
mvn clean test jacoco:report

# Check if jacoco.exec was created
ls -la target/jacoco.exec

# If exists but report not generated
mvn jacoco:report
```

### Issue: "Coverage is 0%"

**Symptom:** JaCoCo shows 0% coverage

**Cause:** JaCoCo agent not attached or tests not running

**Solution:**
```bash
# Verify tests actually ran
# Check target/surefire-reports/

# Ensure argLine is used
mvn clean test -DargLine="${argLine}"

# Check pom.xml for:
<configuration>
    <argLine>${argLine} -Dfile.encoding=UTF-8</argLine>
</configuration>
```

### Issue: "Coverage threshold not met"

**Symptom:** Build fails with `Coverage checks have not been met`

**Cause:** Coverage below threshold

**Solution:**
```bash
# Check current coverage
mvn jacoco:report
open target/site/jacoco/index.html

# Lower threshold temporarily (pom.xml)
<minimum>0.70</minimum>  <!-- Changed from 0.75 -->

# Or add more tests to increase coverage
```

### Issue: "Wrong files included in coverage"

**Symptom:** Test files or generated code in coverage report

**Cause:** JaCoCo exclusions not configured

**Solution:**
```xml
<!-- Add to pom.xml JaCoCo plugin -->
<configuration>
    <excludes>
        <exclude>**/entity/**</exclude>
        <exclude>**/config/**</exclude>
        <exclude>**/Application.class</exclude>
        <exclude>**/*Test.class</exclude>
    </excludes>
</configuration>
```

---

## Webhook/Releason Issues

### Issue: "Webhook not being sent"

**Symptom:** GitHub Actions shows no webhook step or skips it

**Cause:** RELEASON_WEBHOOK_URL secret not set

**Solution:**
```bash
# Check if secret exists
gh secret list

# Add secret
gh secret set RELEASON_WEBHOOK_URL

# Or via GitHub web UI:
# Settings → Secrets → Actions → New repository secret

# Verify in workflow run logs
# Should NOT show: "RELEASON_WEBHOOK_URL not set"
```

### Issue: "Webhook sent but 401/403 error"

**Symptom:** `HTTP 401 Unauthorized` or `HTTP 403 Forbidden`

**Cause:** Invalid or expired webhook URL

**Solution:**
```bash
# Test webhook URL manually
curl -X POST "$WEBHOOK_URL" \
  -H "Content-Type: application/json" \
  -d '{"test": true}'

# If error, regenerate webhook in Releason:
# Dashboard → Settings → Integrations → Regenerate Webhook

# Update GitHub secret with new URL
```

### Issue: "Data not appearing on dashboard"

**Symptom:** Webhook succeeds but metrics don't show in Releason

**Cause:** Data processing delay or payload format issue

**Solution:**
```bash
# Wait 30-60 seconds for processing
# Refresh dashboard page

# Check webhook logs in Releason:
# Dashboard → Settings → Webhook Logs

# Verify payload format:
curl -X POST "$WEBHOOK_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "repository": {"name": "test"},
    "tests": {"passed": 10, "failed": 2, "total": 12},
    "coverage": {"lines_pct": 85}
  }'

# Check for JSON syntax errors
# Use jq to validate: echo '{}' | jq
```

### Issue: "Webhook times out"

**Symptom:** `curl: (28) Operation timed out`

**Cause:** Network connectivity issue

**Solution:**
```bash
# Test connectivity
ping releason.com
curl -I https://releason.com

# Check firewall rules
# Check if proxy is required

# Increase timeout in workflow
curl -X POST --max-time 30 "$WEBHOOK_URL" ...
```

---

## GitHub Actions Issues

### Issue: "Workflow doesn't trigger"

**Symptom:** Push to main but no workflow run appears

**Cause:** Workflow file misconfigured or in wrong location

**Solution:**
```bash
# Verify file location
ls -la .github/workflows/karate-test.yml

# Check YAML syntax
yamllint .github/workflows/karate-test.yml

# Verify triggers
on:
  push:
    branches: [main, develop]

# Check Actions tab for errors
```

### Issue: "Setup Java fails"

**Symptom:** `Error: Could not find or load main class`

**Cause:** Java setup issue

**Solution:**
```yaml
# Use specific Java version
- name: Set up Java 17
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'temurin'
    cache: 'maven'
```

### Issue: "Docker not available in Actions"

**Symptom:** `docker: command not found` in GitHub Actions

**Cause:** Using wrong runner

**Solution:**
```yaml
jobs:
  test:
    runs-on: ubuntu-latest  # Ensure using ubuntu-latest
```

### Issue: "Tests fail in CI but pass locally"

**Symptom:** Tests pass on local machine but fail in GitHub Actions

**Cause:** Environment differences

**Solution:**
```bash
# Check timing differences
# Add longer wait times in CI
if [ "$CI" = "true" ]; then
  sleep 30  # Wait longer in CI
fi

# Check environment variables
env
printenv

# Use same Java/Maven versions locally
```

---

## Performance Issues

### Issue: "Tests are very slow"

**Symptom:** Test suite takes 10+ minutes

**Cause:** Timeouts, network issues, or inefficient tests

**Solution:**
```gherkin
# Reduce delays in tests
* configure connectTimeout = 5000
* configure readTimeout = 5000

# Run tests in parallel
mvn test -Dkarate.options="--threads 4"

# Profile tests to find slow ones
mvn test -Dkarate.options="--tags ~@slow"
```

### Issue: "Docker startup is slow"

**Symptom:** API takes 60+ seconds to start

**Cause:** Large Docker image or resource constraints

**Solution:**
```bash
# Use smaller base image (already using alpine)
# Reduce dependencies in pom.xml
# Increase Docker resources in Docker Desktop settings

# Check startup time
time docker-compose up -d
docker-compose logs -f api | grep "Started Application"
```

### Issue: "High memory usage"

**Symptom:** Container uses >1GB RAM

**Cause:** Memory leaks or inefficient configuration

**Solution:**
```yaml
# Set memory limits
api:
  deploy:
    resources:
      limits:
        memory: 512M

# Adjust JVM options
environment:
  JAVA_OPTS: "-Xmx256m -Xms128m"
```

---

## Getting More Help

### Debug Mode

Enable debug logging:

```bash
# In application.properties
logging.level.root=DEBUG
logging.level.com.example=DEBUG

# For Karate tests
mvn test -Dkarate.options="--debug"
```

### Collect Diagnostic Information

```bash
# System info
uname -a
docker version
docker-compose version
java -version
mvn -version

# Check logs
docker-compose logs > logs.txt
mvn test > test-output.txt 2>&1

# Check running processes
docker ps -a
lsof -i :8080
```

### Where to Get Help

1. **Documentation:**
   - [README.md](../README.md)
   - [SETUP.md](../SETUP.md)
   - [ARCHITECTURE.md](ARCHITECTURE.md)

2. **GitHub Issues:**
   - Search existing issues
   - Create new issue with:
     - Error message
     - Steps to reproduce
     - System info
     - Logs

3. **Releason Support:**
   - Email: support@releason.com
   - Dashboard: Help → Contact Support
   - Include webhook logs

4. **Community:**
   - Karate: [GitHub Discussions](https://github.com/karatelabs/karate/discussions)
   - Spring Boot: [Stack Overflow](https://stackoverflow.com/questions/tagged/spring-boot)
   - Docker: [Docker Forums](https://forums.docker.com/)

---

## Quick Diagnostic Checklist

When something goes wrong, check these in order:

- [ ] Java 17+ installed: `java -version`
- [ ] Maven 3.9+ installed: `mvn -version`
- [ ] Docker running: `docker ps`
- [ ] API healthy: `curl http://localhost:8080/health`
- [ ] Tests can run locally: `mvn test`
- [ ] GitHub secrets configured: `gh secret list`
- [ ] Webhook URL valid: Test with curl
- [ ] Logs checked: `docker-compose logs` and workflow logs
