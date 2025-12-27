@echo off
REM =======================================
REM Fx-Framework Docker Build Script
REM =======================================

echo ======================================
echo Fx-Framework Docker Build Script
echo ======================================

REM Check if Maven exists
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Maven not found. Please install Maven first.
    exit /b 1
)

REM Check if Docker exists
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Docker not found. Please install Docker first.
    exit /b 1
)

REM Build backend JAR
echo.
echo [1/3] Building backend with Maven...
cd demo
call mvn clean package -DskipTests
cd ..

if %errorlevel% neq 0 (
    echo [ERROR] Maven build failed.
    exit /b 1
)

echo [SUCCESS] Backend JAR built successfully.

REM Build Docker images
echo.
echo [2/3] Building Docker images...

echo Building backend image...
docker build -t fx-framework-backend:latest -f demo/Dockerfile demo

if %errorlevel% neq 0 (
    echo [ERROR] Backend image build failed.
    exit /b 1
)

echo [SUCCESS] Backend image built successfully.

echo Building admin image...
docker build -t fx-framework-admin:latest -f demo-admin/Dockerfile demo-admin

if %errorlevel% neq 0 (
    echo [ERROR] Admin image build failed.
    exit /b 1
)

echo [SUCCESS] Admin image built successfully.

echo.
echo [3/3] Displaying built images...
docker images | findstr fx-framework

echo.
echo ======================================
echo All Docker images built successfully!
echo ======================================
echo.
echo To start all services, run:
echo   docker-compose up -d
echo.
echo To view logs:
echo   docker-compose logs -f
echo.
