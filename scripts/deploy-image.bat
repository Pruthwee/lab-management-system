@echo off
setlocal enabledelayedexpansion

rem ============================================================
rem deploy-image.bat - Deploy Lab Management System to AWS EKS
rem ============================================================

set APP_NAME=lab-management-system
set NAMESPACE=lab-management-system
set K8S_DIR=kubernetes

echo ============================================================
echo   Lab Management System - AWS EKS Deployment
echo ============================================================

rem Prompt for AWS configuration
echo.
set /p AWS_REGION="Enter AWS Region (e.g. us-east-1): "
if "!AWS_REGION!"=="" (
  echo ERROR: AWS Region is required.
  exit /b 1
)

set /p CLUSTER_NAME="Enter EKS Cluster Name: "
if "!CLUSTER_NAME!"=="" (
  echo ERROR: EKS Cluster Name is required.
  exit /b 1
)

set /p IMAGE_URI="Enter full Docker Image URI (e.g. 123456789.dkr.ecr.us-east-1.amazonaws.com/lab-management-system:latest): "
if "!IMAGE_URI!"=="" (
  echo ERROR: Docker Image URI is required.
  exit /b 1
)

rem Prompt for application environment variables
echo.
echo ------------------------------------------------------------
echo   Application Environment Variables (press Enter to skip)
echo ------------------------------------------------------------

set /p DB_HOST="Enter DB_HOST (database hostname): "
set /p DB_PORT="Enter DB_PORT (default: 5432): "
set /p DB_NAME="Enter DB_NAME (default: labdb): "
set /p DB_USERNAME="Enter DB_USERNAME (default: postgres): "
set /p DB_PASSWORD="Enter DB_PASSWORD: "
set /p DB_URL="Enter DB_URL (or leave blank to auto-build): "
set /p API_BASE_URL="Enter API_BASE_URL (e.g. https://lab-management-system.example.com): "
set /p EQUIPMENT_DEFAULT_IP="Enter EQUIPMENT_DEFAULT_IP (default: 192.168.1.100): "
set /p EQUIPMENT_DEFAULT_PORT="Enter EQUIPMENT_DEFAULT_PORT (default: 8888): "

rem Set defaults
if "!DB_HOST!"=="" set DB_HOST=localhost
if "!DB_PORT!"=="" set DB_PORT=5432
if "!DB_NAME!"=="" set DB_NAME=labdb
if "!DB_USERNAME!"=="" set DB_USERNAME=postgres
if "!DB_URL!"=="" set DB_URL=jdbc:postgresql://!DB_HOST!:!DB_PORT!/!DB_NAME!
if "!API_BASE_URL!"=="" set API_BASE_URL=http://localhost:8080
if "!EQUIPMENT_DEFAULT_IP!"=="" set EQUIPMENT_DEFAULT_IP=192.168.1.100
if "!EQUIPMENT_DEFAULT_PORT!"=="" set EQUIPMENT_DEFAULT_PORT=8888

echo.
echo ------------------------------------------------------------
echo   Configuring kubectl for EKS cluster: !CLUSTER_NAME!
echo ------------------------------------------------------------
aws eks update-kubeconfig --region !AWS_REGION! --name !CLUSTER_NAME!
if !ERRORLEVEL! neq 0 (
  echo ERROR: Failed to configure kubectl.
  exit /b 1
)

echo Verifying cluster connectivity...
kubectl cluster-info
if !ERRORLEVEL! neq 0 (
  echo ERROR: Cannot connect to cluster.
  exit /b 1
)

echo.
echo ------------------------------------------------------------
echo   Updating Kubernetes manifests with deployment values
echo ------------------------------------------------------------

rem Use PowerShell to replace placeholders in deployment.yaml
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{IMAGE_URI}}','!IMAGE_URI!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{DB_HOST}}','!DB_HOST!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{DB_PORT}}','!DB_PORT!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{DB_NAME}}','!DB_NAME!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{DB_USERNAME}}','!DB_USERNAME!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{DB_PASSWORD}}','!DB_PASSWORD!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{DB_URL}}','!DB_URL!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{API_BASE_URL}}','!API_BASE_URL!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{EQUIPMENT_DEFAULT_IP}}','!EQUIPMENT_DEFAULT_IP!' | Set-Content '!K8S_DIR!\deployment.yaml'"
powershell -Command "(Get-Content '!K8S_DIR!\deployment.yaml') -replace '{{EQUIPMENT_DEFAULT_PORT}}','!EQUIPMENT_DEFAULT_PORT!' | Set-Content '!K8S_DIR!\deployment.yaml'"

echo.
echo ------------------------------------------------------------
echo   Applying Kubernetes manifests
echo ------------------------------------------------------------

echo Applying namespace...
kubectl apply -f !K8S_DIR!\namespace.yaml
if !ERRORLEVEL! neq 0 ( echo ERROR: Failed to apply namespace. & exit /b 1 )

echo Applying deployment...
kubectl apply -f !K8S_DIR!\deployment.yaml
if !ERRORLEVEL! neq 0 ( echo ERROR: Failed to apply deployment. & exit /b 1 )

echo Applying service...
kubectl apply -f !K8S_DIR!\service.yaml
if !ERRORLEVEL! neq 0 ( echo ERROR: Failed to apply service. & exit /b 1 )

echo Applying ingress...
kubectl apply -f !K8S_DIR!\ingress.yaml
if !ERRORLEVEL! neq 0 ( echo ERROR: Failed to apply ingress. & exit /b 1 )

echo.
echo ------------------------------------------------------------
echo   Waiting for deployment rollout...
echo ------------------------------------------------------------
kubectl rollout status deployment/!APP_NAME! -n !NAMESPACE! --timeout=300s
if !ERRORLEVEL! neq 0 (
  echo ERROR: Deployment rollout failed.
  echo Rollback command: kubectl rollout undo deployment/!APP_NAME! -n !NAMESPACE!
  exit /b 1
)

echo.
echo ------------------------------------------------------------
echo   Verifying deployed resources
echo ------------------------------------------------------------
kubectl get pods,svc,ingress -n !NAMESPACE!

echo.
echo ------------------------------------------------------------
echo   Deployment complete!
echo ------------------------------------------------------------
echo   Rollback command (if needed):
echo     kubectl rollout undo deployment/!APP_NAME! -n !NAMESPACE!
echo ============================================================

endlocal
