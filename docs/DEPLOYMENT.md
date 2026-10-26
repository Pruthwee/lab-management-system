# Deployment Guide - Laboratory Management System

## Overview

This document describes how to build, deploy, and manage the Laboratory Management System on AWS ECS Fargate. The application is a Spring Boot 3.1.5 microservice running on Java 17, packaged as a runnable JAR.

## Prerequisites

### System Requirements
- **Java**: 17 (Eclipse Temurin)
- **Build Tool**: Maven 3.9+
- **Docker**: 20.10+
- **AWS CLI**: 2.0+
- **Operating System**: Linux or macOS (Windows with WSL2 recommended)

### AWS Prerequisites
- An AWS account with permissions to create ECS clusters, ECR repositories, IAM roles, VPC resources, and Secrets Manager secrets
- A VPC with at least two private or public subnets
- A security group that allows inbound TCP traffic on port 8080
- IAM roles (`ecsTaskExecutionRole` and `ecsTaskRole`) - see [IAM Roles](#iam-roles) below

## Local Setup with Docker Compose

1. Build and run the application locally:
   ```bash
   docker-compose up --build
   ```

2. The application will be available at `http://localhost:8080`
3. Health check endpoint: `http://localhost:8080/health`
4. API info endpoint: `http://localhost:8080/`

### Environment Variables (Docker Compose)

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring profile to activate |
| `SERVER_PORT` | `8080` | Application server port |
| `DB_URL` | `jdbc:h2:mem:testdb` | Database JDBC URL |
| `DB_USERNAME` | `sa` | Database username |
| `DB_PASSWORD` | (empty) | Database password |
| `DATA_PATH` | `/app/data` | Path for file storage (EFS-backed in production) |
| `BACKUP_PATH` | `/app/backup` | Path for backup files |
| `REPORTS_PATH` | `/app/reports` | Path for report files |
| `LOG_PATH` | `/app/logs` | Path for log files |
| `EQUIPMENT_IP` | `192.168.1.100` | Default equipment IP address |
| `EQUIPMENT_PORT` | `8888` | Default equipment port |
| `SERVER_HOST` | `10.0.0.50` | Server host for equipment communication |
| `SERVER_PORT` | `5000` | Server port for equipment communication |
| `API_BASE_URL` | `http://localhost:8080` | Base URL for API |

## Building and Pushing the Docker Image

### Using the build-push Script

**Linux/macOS:**
```bash
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

**Windows:**
```cmd
scripts\build-push.bat
```

The script will:
1. Prompt for the target platform (AWS ECR or Docker Hub)
2. Prompt for the image tag (defaults to `latest`)
3. Log in to the chosen registry
4. Build the Docker image using the multi-stage Dockerfile
5. Push the image to the registry

### Manual Build

```bash
docker build -f Dockerfile -t lab-management-system:latest .
```

## AWS ECS Fargate Deployment

### IAM Roles

The deployment requires two IAM roles:

1. **ecsTaskExecutionRole** (`arn:aws:iam::{{ACCOUNT_ID}}:role/ecsTaskExecutionRole`)
   - Permissions: `AmazonECSTaskExecutionRolePolicy` (for ECR pull and CloudWatch Logs)
   - Additional: `secretsmanager:GetSecretValue` on secrets matching `lab-management-system/*`

2. **ecsTaskRole** (`arn:aws:iam::{{ACCOUNT_ID}}:role/ecsTaskRole`)
   - Permissions for EFS access: `elasticfilesystem:ClientMount` and `elasticfilesystem:ClientWrite` on the EFS resource
   - SSM permissions for ECS Exec debugging

### Fargate Setup

#### Step 1: Create EFS Filesystem

```bash
# Create EFS filesystem
aws efs create-file-system \
    --creation-token lab-management-system-efs \
    --performance-mode generalPurpose \
    --throughput-mode bursting \
    --encrypted \
    --region us-east-1

# Note the FileSystemId from the output
# Create mount targets in your subnets
# Create an access point scoped to /app/data
```

#### Step 2: Create CloudWatch Log Group

```bash
aws logs create-log-group --log-group-name /ecs/lab-management-system
```

#### Step 3: Create Secrets in Secrets Manager

```bash
aws secretsmanager create-secret --name lab-management-system/DB_URL --secret-string "jdbc:postgresql://host:5432/db"
aws secretsmanager create-secret --name lab-management-system/DB_USERNAME --secret-string "username"
aws secretsmanager create-secret --name lab-management-system/DB_PASSWORD --secret-string "password"
```

#### Step 4: Deploy Using the deploy-image Script

**Linux/macOS:**
```bash
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh
```

**Windows:**
```cmd
scripts\deploy-image.bat
```

The script will:
1. Prompt for AWS region, cluster name, VPC ID, subnet IDs, and security group ID
2. Ask whether a load balancer is needed
3. Ask for credential values (DB_URL, DB_USERNAME, DB_PASSWORD) and store them in Secrets Manager
4. Create the ECS cluster if it doesn't exist
5. Create the CloudWatch log group if it doesn't exist
6. Create an Application Load Balancer and target group if requested
7. Register the task definition with the filled-in placeholders
8. Create or update the ECS service
9. Wait for the service to stabilize
10. Display service details and troubleshooting hints

### Task Definition Explained

The ECS task definition (`ecs/task-definition.json`) includes:

- **Fargate Compatibility**: `requiresCompatibilities: ["FARGATE"]` with `networkMode: "awsvpc"`
- **CPU and Memory**: `cpu: "512"` (0.5 vCPU), `memory: "1024"` (1 GB)
- **Container Definition**: Runs the JAR on port 8080 with environment variables for configuration
- **EFS Volume**: Mounts an EFS filesystem at `/app/data` for persistent file storage
- **Secrets**: Database credentials are injected from AWS Secrets Manager, never stored in plaintext
- **Logging**: Uses `awslogs` driver to send logs to CloudWatch Logs at `/ecs/lab-management-system`

### Service Configuration

The ECS service (`ecs/service-definition.json`) includes:

- **Fargate Launch Type**: `launchType: "FARGATE"`
- **awsvpc Networking**: Two subnets for high availability, public IP assignment
- **Desired Count**: 2 tasks for redundancy
- **Deployment Configuration**: `maximumPercent: 200`, `minimumHealthyPercent: 50` for rolling updates
- **Load Balancer**: Application Load Balancer with target group on port 8080
- **Health Check Grace Period**: 300 seconds to allow JVM startup

### Walkthrough

1. Build and push the image using `scripts/build-push.sh`
2. Run `scripts/deploy-image.sh` and provide the requested values
3. The script creates all necessary AWS resources
4. Wait for the service to reach `ACTIVE` state
5. Access the application via the load balancer DNS name (if a load balancer was created)

## Configuration Management

All configuration is externalized through environment variables. The application reads configuration from:

1. **Environment variables** (injected by ECS task definition)
2. **Spring Boot properties** (with environment variable fallbacks using `${VAR:default}` syntax)
3. **AWS Secrets Manager** (for credentials, injected via ECS secrets block)

The `SPRING_PROFILES_ACTIVE` environment variable controls which Spring profile is active. The `prod` profile is used in production.

## Security Considerations

- **Non-root user**: The Docker image runs as `appuser`, not root
- **Secrets management**: Database credentials are stored in AWS Secrets Manager, never in the task definition or source code
- **EFS encryption**: EFS filesystem should have encryption at rest and in transit enabled
- **Network isolation**: Security groups should restrict inbound traffic to only necessary ports
- **IAM least privilege**: Task roles should have minimum required permissions
- **Health endpoint**: The `/health` endpoint returns basic status without exposing sensitive information

## Java Notes

### JVM Settings

The application runs with the following JVM settings optimized for container environments:

```
-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
```

- `-Xmx512m`: Maximum heap size of 512 MB
- `-Xms256m`: Initial heap size of 256 MB
- `-XX:+UseContainerSupport`: Enables container-aware memory limits (default in JDK 10+)
- `-XX:MaxRAMPercentage=75.0`: Uses 75% of the container's memory limit for the heap

For production with the default Fargate configuration (1024 MB memory), the JVM will use approximately 768 MB for the heap, leaving ~256 MB for native memory and off-heap allocations. Adjust `-Xmx` and `MaxRAMPercentage` if your application requires more memory.

### Spring Profiles

- **prod** (default in container): Production settings with WARN logging level
- **dev**: Development settings with DEBUG logging
- **test**: Test settings with H2 console enabled

Set `SPRING_PROFILES_ACTIVE=prod` in the ECS task definition environment.

### Health Endpoint

The application exposes a health check endpoint at `/health` (GET):

```json
{
  "status": "UP",
  "timestamp": "Mon Oct 26 14:04:39 UTC 2026"
}
```

The Application Load Balancer's target group health check is configured to check `http://127.0.0.1:8080/health` within the container. This endpoint is available from application startup and runs for the lifetime of the process.

### File Storage

The application uses environment-variable-resolved file paths for persistent storage:
- `DATA_PATH` (default: `/app/data`) - Primary data storage, EFS-backed in production
- `BACKUP_PATH` (default: `/app/backup`) - Backup file storage
- `REPORTS_PATH` (default: `/app/reports`) - Report output storage

In the Fargate task definition, the EFS volume is mounted at `/app/data` to ensure file persistence across task restarts. The `DATA_PATH` environment variable is set to `/app/data` in the task definition to match the mount point.

### Database

The application uses Spring Data JPA with an H2 database for development and testing. In production, the database URL, username, and password are injected via environment variables from AWS Secrets Manager. The H2 database is used by the project's own tests and is part of the project's test infrastructure.
