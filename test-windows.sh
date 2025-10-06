#!/bin/bash

# Build and Test Windows App-Image with Docker
# This script builds the Windows app-image and tests it using Wine in Docker

set -e

echo "========================================"
echo "Building Windows App-Image..."
echo "========================================"

# Build the Windows app-image
./gradlew clean jpackage -PtargetOs=windows

echo ""
echo "========================================"
echo "Building Docker Image..."
echo "========================================"

# Build Docker image
docker build -t htmltopdf-windows-test .

echo ""
echo "========================================"
echo "Running Windows App in Docker..."
echo "========================================"

# Run the container
# Add -p 5900:5900 if you want to connect via VNC to see the GUI
docker run --rm -it htmltopdf-windows-test

echo ""
echo "========================================"
echo "Test Complete!"
echo "========================================"

