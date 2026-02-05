# Detailed Setup Guide

Complete step-by-step instructions for setting up the Releason + Karate demo.

## System Requirements

### Operating Systems
- macOS 10.15+
- Linux (Ubuntu 20.04+, Debian 10+, etc.)
- Windows 10/11 with WSL2

### Required Software
- **Java 17 or higher**
  - Download from [Adoptium](https://adoptium.net/)
  - Verify: `java -version`
- **Maven 3.9 or higher**
  - Download from [Maven](https://maven.apache.org/download.cgi)
  - Verify: `mvn -version`
- **Docker Desktop**
  - Download from [Docker](https://www.docker.com/products/docker-desktop)
  - Verify: `docker --version` and `docker-compose --version`
- **Git**
  - Download from [Git](https://git-scm.com/downloads)
  - Verify: `git --version`

### Hardware Requirements
- **RAM:** Minimum 4GB, recommended 8GB+
- **Disk Space:** 2GB free space
- **CPU:** 2+ cores recommended

## Installation Steps

### Step 1: Install Java 17

#### macOS
```bash
# Using Homebrew
brew install openjdk@17

# Set JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```

#### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

#### Windows (WSL2)
```bash
# In WSL2 terminal
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Verify Installation
```bash
java -version
# Should show: openjdk version "17.0.x"
```

### Step 2: Install Maven

#### macOS
```bash
brew install maven
```

#### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install maven

# Or download manually
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar xzvf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven
echo 'export PATH=/opt/maven/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

#### Verify Installation
```bash
mvn -version
# Should show: Apache Maven 3.9.x
```

### Step 3: Install Docker Desktop

#### macOS
1. Download Docker Desktop from [docker.com](https://www.docker.com/products/docker-desktop)
2. Install the .dmg file
3. Start Docker Desktop from Applications
4. Wait for Docker to start (whale icon in menu bar)

#### Linux
```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER
newgrp docker

# Install docker-compose
sudo apt install docker-compose-plugin
```

#### Windows
1. Enable WSL2
2. Download Docker Desktop from [docker.com](https://www.docker.com/products/docker-desktop)
3. Install and restart
4. Ensure "Use WSL2 based engine" is enabled in settings

#### Verify Installation
```bash
docker --version
docker-compose --version
docker run hello-world
```

### Step 4: Clone the Repository

```bash
# Clone the repository
git clone https://github.com/tqsystems/releason-karate-demo
cd releason-karate-demo

# Verify files
ls -la
# Should see: pom.xml, docker-compose.yml, src/, etc.
```

### Step 5: Configure Environment (Optional)

```bash
# Copy environment template
cp .env.example .env

# Edit with your Releason webhook URL
nano .env  # or use your preferred editor

# Add your webhook URL:
# RELEASON_WEBHOOK_URL=https://releason.com/api/webhooks/your-webhook-id
```

> **Note:** Releason webhook URL is optional. The demo works without it, but you won't see metrics on the Releason dashboard.

### Step 6: Start Docker Services

```bash
# Start the API server
docker-compose up -d

# Check status
docker-compose ps
# Should show: releason-karate-demo-api running

# View logs
docker-compose logs -f api
# Wait for: "Started Application in X.XXX seconds"
```

### Step 7: Verify API is Running

```bash
# Check health endpoint
curl http://localhost:8080/health

# Expected output:
# {"status":"UP","timestamp":"2026-01-30T...","service":"releason-karate-demo"}

# Test API endpoints
curl http://localhost:8080/api/users
# Should return array of users
```

### Step 8: Run Tests

```bash
# Run all tests with coverage
mvn clean test

# Expected output:
# Tests run: 14, Failures: 2, Errors: 0, Skipped: 0
# Some tests will fail intentionally to demonstrate risk detection
```

### Step 9: View Coverage Report

```bash
# Generate HTML report
mvn jacoco:report

# Open in browser
# macOS:
open target/site/jacoco/index.html

# Linux:
xdg-open target/site/jacoco/index.html

# Windows (WSL2):
explorer.exe target/site/jacoco/index.html
```

## Verification Checklist

After setup, verify everything works:

- [ ] `java -version` shows Java 17+
- [ ] `mvn -version` shows Maven 3.9+
- [ ] `docker --version` and `docker-compose --version` work
- [ ] `docker-compose up -d` starts successfully
- [ ] `curl http://localhost:8080/health` returns `{"status":"UP"}`
- [ ] `mvn clean test` runs tests (some fail intentionally)
- [ ] Coverage report opens in browser

## Common Setup Issues

### Issue: "java: command not found"
**Solution:** Java not installed or not in PATH
```bash
# Check JAVA_HOME
echo $JAVA_HOME

# Set JAVA_HOME (macOS)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Set JAVA_HOME (Linux)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

### Issue: "mvn: command not found"
**Solution:** Maven not installed or not in PATH
```bash
# Check Maven location
which mvn

# Add to PATH (if installed in /opt/maven)
export PATH=/opt/maven/bin:$PATH
```

### Issue: "docker: Cannot connect to the Docker daemon"
**Solution:** Docker not running
```bash
# macOS: Start Docker Desktop application
# Linux: Start Docker service
sudo systemctl start docker

# Verify
docker ps
```

### Issue: "Port 8080 already in use"
**Solution:** Another service using port 8080
```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Stop the process or change port in docker-compose.yml
```

### Issue: Tests fail with connection errors
**Solution:** API not started or not ready
```bash
# Wait longer for API to start
sleep 10

# Check API logs
docker-compose logs api

# Restart API
docker-compose restart api
```

## Next Steps

After successful setup:

1. ✅ Read [RELEASON_INTEGRATION.md](RELEASON_INTEGRATION.md) to connect to Releason
2. ✅ Read [DASHBOARD_WALKTHROUGH.md](DASHBOARD_WALKTHROUGH.md) to understand metrics
3. ✅ Read [EXTENDING.md](EXTENDING.md) to customize for your needs
4. ✅ Explore the code in `src/` directory
5. ✅ Experiment with modifying tests and seeing coverage change

## Need Help?

- **Documentation:** See [README.md](README.md) and [docs/](docs/) folder
- **Troubleshooting:** See [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)
- **Issues:** Create a GitHub issue
- **Support:** Contact Releason support team