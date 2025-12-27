#!/bin/bash

# =======================================
# Fx-Framework Docker Build Script
# =======================================

echo ======================================
echo Fx-Framework Docker Build Script
echo ======================================

# Check if Maven exists
if ! command -v mvn &> /dev/null; then
    echo "[ERROR] Maven not found. Please install Maven first."
    exit 1
fi

# Check if Docker exists
if ! command -v docker &> /dev/null; then
    echo "[ERROR] Docker not found. Please install Docker first."
    exit 1
fi

# Build backend JAR
echo
echo "[1/3] Building backend with Maven..."
cd demo
mvn clean package -DskipTests
cd ..

if [ $? -ne 0 ]; then
    echo "[ERROR] Maven build failed."
    exit 1
fi

echo "[SUCCESS] Backend JAR built successfully."

# Build Docker images
echo
echo "[2/3] Building Docker images..."

echo "Building backend image..."
docker build -t fx-framework-backend:latest -f demo/Dockerfile demo

if [ $? -ne 0 ]; then
    echo "[ERROR] Backend image build failed."
    exit 1
fi

echo "[SUCCESS] Backend image built successfully."

echo "Building admin image..."
docker build -t fx-framework-admin:latest -f demo-admin/Dockerfile demo-admin

if [ $? -ne 0 ]; then
    echo "[ERROR] Admin image build failed."
    exit 1
fi

echo "[SUCCESS] Admin image built successfully."

echo
echo "[3/3] Displaying built images..."
docker images | grep fx-framework

echo
echo ======================================
echo All Docker images built successfully!
echo ======================================
echo
echo To start all services, run:
echo   docker-compose up -d
echo
echo To view logs:
echo   docker-compose logs -f
echo
