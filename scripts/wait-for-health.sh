#!/bin/bash

# wait-for-health.sh
# Waits for the API server to be ready by checking the health endpoint

set -e

HOST="${1:-localhost}"
PORT="${2:-8080}"
MAX_ATTEMPTS="${3:-60}"
SLEEP_INTERVAL="${4:-2}"

echo "Waiting for API server at http://${HOST}:${PORT}/health to be ready..."
echo "Max attempts: $MAX_ATTEMPTS (sleeping ${SLEEP_INTERVAL}s between attempts)"

for i in $(seq 1 $MAX_ATTEMPTS); do
  if curl -f -s "http://${HOST}:${PORT}/health" > /dev/null 2>&1; then
    echo "✅ API server is ready! (attempt $i/$MAX_ATTEMPTS)"
    
    # Verify response contains expected data
    RESPONSE=$(curl -s "http://${HOST}:${PORT}/health")
    if echo "$RESPONSE" | grep -q "UP"; then
      echo "✅ Health check passed: $RESPONSE"
      exit 0
    else
      echo "⚠️  Health endpoint responded but status is not UP: $RESPONSE"
    fi
  fi
  
  echo "⏳ Attempt $i/$MAX_ATTEMPTS... waiting for server (sleeping ${SLEEP_INTERVAL}s)"
  sleep $SLEEP_INTERVAL
done

echo "❌ API server failed to start after $MAX_ATTEMPTS attempts"
echo ""
echo "Troubleshooting tips:"
echo "1. Check if Docker container is running: docker ps"
echo "2. Check container logs: docker-compose logs api"
echo "3. Check if port $PORT is available: lsof -i :$PORT"
echo "4. Try starting manually: docker-compose up"

exit 1
