#!/bin/bash

set -e

echo "------------------------------------------------"
echo "--- Starting Update Auto-Build Process ---"
echo "------------------------------------------------"

echo ">> [STEP 1/4] Installing Vue Dependencies..."
npm --prefix ./vite install

echo ">> [STEP 2/4] Building HTML,CSS,JS Assets..."
npm --prefix ./vite run build

echo ">> [STEP 3/4] Cleaning and Packaging Java..."
./mvnw clean package

echo ">> [STEP 4/4] Running up Java..."
java -jar ./target/base-0.0.1-SNAPSHOT.jar

echo "------------------------------------------------"
echo "---Finished Update Auto-Build Process ---"
echo "------------------------------------------------"