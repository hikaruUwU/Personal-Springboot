#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

echo "------------------------------------------------"
echo "--- Starting Update Auto-Build Process ---"
echo "------------------------------------------------"

# --- NEW STEP: Check and Stop Old Service ---
echo ">> [STEP 0/5] Checking and stopping old service on port 8080..."

PID_8080=$(ss -tuln sport = :8080 | awk 'NR>1 {print $6}' | sed 's/.*,pid=//;s/,.*//' | head -n 1 2>/dev/null)

if [ -n "$PID_8080" ]; then
    echo "Old process found on port 8080 (PID: $PID_8080). Attempting graceful shutdown (SIGTERM)..."
    kill -9 "$PID_8080"

    # Wait for the process to stop gracefully
    sleep 5

    # Check if the process is still running after 5 seconds
    if kill -0 "$PID_8080" 2>/dev/null; then
        echo "Process $PID_8080 still running. Forcing termination (SIGKILL)..."
        kill -9 "$PID_8080"
    else
        echo "Process $PID_8080 stopped successfully."
    fi
else
    echo "No old service found listening on port 8080."
fi

echo ">> [STEP 1/5] Installing Vue Dependencies..."
npm --prefix ./vite install

echo ">> [STEP 2/5] Building HTML,CSS,JS Assets..."
npm --prefix ./vite run build

echo ">> [STEP 3/5] Cleaning and Packaging Java..."
./mvnw clean package -DskipTests

echo ">> [STEP 4/5] Running up Java..."
# 动态查找最新生成的 JAR 文件名
# ls -t 按时间倒序排序，head -n 1 取出最新的 JAR 文件路径
JAR_PATH=$(ls -t ./target/*.jar 2>/dev/null | head -n 1)

if [ -z "$JAR_PATH" ]; then
    echo "Error: Failed to find the packaged executable JAR file in ./target/"
    exit 1
fi

echo "Starting artifact: $JAR_PATH"
java -jar "$JAR_PATH"

echo "------------------------------------------------"
echo "---Finished Update Auto-Build Process ---"
echo "------------------------------------------------"