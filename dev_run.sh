#!/bin/bash

set -e

echo "------------------------------------------------"
echo "--- Starting Update Auto-Build Process ---"
echo "------------------------------------------------"

echo ">> [STEP 1/3] Installing Vue Dependencies..."
npm --prefix ./vite install

echo ">> [STEP 2/3] Building HTML,CSS,JS Assets..."
npm --prefix ./vite run build

echo ">> [STEP 3/3] Cleaning and Packaging Java..."
./mvnw clean package

echo "------------------------------------------------"
echo "---Finished Update Auto-Build Process ---"
echo "------------------------------------------------"