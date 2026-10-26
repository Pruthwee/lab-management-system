@echo off
setlocal enabledelayedexpansion

set "PROJECT_NAME=lab-management-system"
set "IMAGE_NAME=%PROJECT_NAME%"

echo === Build and Push Script for %IMAGE_NAME% ===
echo.

echo Select target platform:
echo 1. AWS ECR
echo 2. Docker Hub
set /p PLATFORM_CHOICE="Enter choice (1 or 2): "

set /p IMAGE_TAG="Enter IMAGE_TAG (leave empty for 'latest'): "
if "%IMAGE_TAG%"=="" (
    set "IMAGE_TAG=latest"
)

if "%PLATFORM_CHOICE%"=="1" (
    set /p AWS_REGION="Enter AWS region (e.g., us-east-1): "
    set /p ACCOUNT_ID="Enter AWS account ID: "
    set /p ECR_REPO="Enter ECR repository name (default: %IMAGE_NAME%): "
    if "!ECR_REPO!"=="" (
        set "ECR_REPO=%IMAGE_NAME%"
    )

    set "REGISTRY_URL=!ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com"
    set "FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!"

    echo.
    echo === Logging in to Amazon ECR ===
    call aws ecr get-login-password --region !AWS_REGION! | docker login --username AWS --password-stdin !REGISTRY_URL!
    if !ERRORLEVEL! neq 0 (
        echo ECR login failed
        exit /b 1
    )

    echo.
    echo === Creating ECR repository if needed ===
    call aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
    if !ERRORLEVEL! neq 0 (
        echo Creating ECR repository...
        call aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION!
    )

    echo.
    echo === Building Docker image ===
    echo Image: !FULL_IMAGE_NAME!
    docker build -f Dockerfile -t !FULL_IMAGE_NAME! .
    if !ERRORLEVEL! neq 0 (
        echo Docker build failed
        exit /b 1
    )

    echo.
    echo === Pushing Docker image ===
    docker push !FULL_IMAGE_NAME!
    if !ERRORLEVEL! neq 0 (
        echo Docker push failed
        exit /b 1
    )

    echo.
    echo === Success ===
    echo Image pushed to: !FULL_IMAGE_NAME!

) else if "%PLATFORM_CHOICE%"=="2" (
    set /p DOCKER_USERNAME="Enter Docker Hub username: "
    set /p DOCKER_PASSWORD="Enter Docker Hub password: "

    set "FULL_IMAGE_NAME=%DOCKER_USERNAME%/%IMAGE_NAME%:%IMAGE_TAG%"

    echo.
    echo === Logging in to Docker Hub ===
    echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
    if !ERRORLEVEL! neq 0 (
        echo Docker Hub login failed
        exit /b 1
    )

    echo.
    echo === Building Docker image ===
    echo Image: !FULL_IMAGE_NAME!
    docker build -f Dockerfile -t !FULL_IMAGE_NAME! .
    if !ERRORLEVEL! neq 0 (
        echo Docker build failed
        exit /b 1
    )

    echo.
    echo === Pushing Docker image ===
    docker push !FULL_IMAGE_NAME!
    if !ERRORLEVEL! neq 0 (
        echo Docker push failed
        exit /b 1
    )

    echo.
    echo === Success ===
    echo Image pushed to: !FULL_IMAGE_NAME!

) else (
    echo Invalid choice. Exiting.
    exit /b 1
)

endlocal
