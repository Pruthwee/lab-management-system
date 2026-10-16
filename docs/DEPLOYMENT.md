# Lab Management System - Deployment Guide

## Overview

This guide covers the complete deployment process for the **Lab Management System** — a Spring Boot 3.1.5 Java 17 REST API for laboratory management — targeting **AWS EKS (Elastic Kubernetes Service)**.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Structure](#project-structure)
3. [Local Development with Docker Compose](#local-development-with-docker-compose)
4. [Build and Push Docker Image](#build-and-push-docker-image)
5. [AWS EKS Prerequisites](#aws-eks-prerequisites)
6. [EKS Cluster Setup](#eks-cluster-setup)
7. [Kubernetes Deployment Walkthrough](#kubernetes-deployment-walkthrough)
8. [Deploy Using Scripts](#deploy-using-scripts)
9. [Configuration Management](#configuration-management)
10. [EKS Scaling and Management](#eks-scaling-and-management)
11. [Troubleshooting](#troubleshooting)
12. [Security Considerations](#security-considerations)
13. [Java-Specific Notes](#java-specific-notes)

---

## Prerequisites

### Local Development Tools
- **Docker** 24.x or later
- **Docker Compose** v2.x or later
- **Java 17** (eclipse-temurin or equivalent)
- **Maven 3.9.x**

### AWS EKS Deployment Tools
- **AWS CLI** v2.x — [Install Guide](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html)
- **kubectl** v1.28+ — [Install Guide](https://kubernetes.io/docs/tasks/tools/)
- **eksctl** (optional, for cluster creation) — [Install Guide](https://eksctl.io/)
- **AWS IAM permissions**: ECR push, EKS cluster access, ALB controller permissions

### AWS IAM Permissions Required
```json
{
  "Version": "2012-10-17",
  "Statement": [
    { "Effect": "Allow", "Action": ["ecr:*"], "Resource": "*" },
    { "Effect": "Allow", "Action": ["eks:DescribeCluster", "eks:ListClusters"], "Resource": "*" },
    { "Effect": "Allow", "Action": ["elasticloadbalancing:*"], "Resource": "*" }
  ]
}
```

---

## Project Structure

```
lab_mgmt_comp_mcont_1857/
├── Dockerfile                    # Multi-stage Docker build
├── docker-compose.yml            # Local development compose file
├── .dockerignore                 # Docker build exclusions
├── pom.xml                       # Maven build descriptor
├── src/                          # Application source code
├── kubernetes/
│   ├── namespace.yaml            # Kubernetes namespace
│   ├── deployment.yaml           # Application deployment
│   ├── service.yaml              # ClusterIP service
│   └── ingress.yaml              # AWS ALB ingress
├── scripts/
│   ├── build-push.sh             # Linux/macOS build & push
│   ├── build-push.bat            # Windows build & push
│   ├── deploy-image.sh           # Linux/macOS EKS deploy
│   └── deploy-image.bat          # Windows EKS deploy
└── docs/
    └── DEPLOYMENT.md             # This file
```

---

## Local Development with Docker Compose

### 1. Configure Environment Variables

Create a `.env` file in the project root:

```env
# Database (provide your PostgreSQL connection details)
DB_HOST=your-postgres-host
DB_PORT=5432
DB_NAME=labdb
DB_USERNAME=postgres
DB_PASSWORD=your-password
DB_URL=jdbc:postgresql://your-postgres-host:5432/labdb

# Application
API_BASE_URL=http://localhost:8080
LAB_DATA_PATH=/app/data
LAB_BACKUP_PATH=/app/backup
LAB_REPORTS_PATH=/app/reports

# Equipment
EQUIPMENT_DEFAULT_IP=192.168.1.100
EQUIPMENT_DEFAULT_PORT=8888
```

### 2. Build and Start

```bash
# Build and start the application container
docker-compose up --build

# Run in background
docker-compose up -d --build

# View logs
docker-compose logs -f lab-management-system

# Stop
docker-compose down
```

### 3. Verify Application

```bash
# Health check
curl http://localhost:8080/health

# API info
curl http://localhost:8080/
```

---

## Build and Push Docker Image

### Linux/macOS

```bash
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

The script will prompt you to:
1. Select registry (AWS ECR or Docker Hub)
2. Enter registry credentials/details
3. Enter image tag (defaults to `latest`)

### Windows

```cmd
scripts\build-push.bat
```

### Manual Build (AWS ECR)

```bash
# Authenticate
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789012.dkr.ecr.us-east-1.amazonaws.com

# Create repository (first time)
aws ecr create-repository --repository-name lab-management-system --region us-east-1

# Build
docker build -t lab-management-system:latest .

# Tag
docker tag lab-management-system:latest \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/lab-management-system:latest

# Push
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/lab-management-system:latest
```

---

## AWS EKS Prerequisites

### 1. Configure AWS CLI

```bash
aws configure
# Enter: AWS Access Key ID, Secret Access Key, Region, Output format
```

### 2. Install AWS Load Balancer Controller

The ingress uses the AWS Load Balancer Controller. Install it on your EKS cluster:

```bash
# Add Helm repo
helm repo add eks https://aws.github.io/eks-charts
helm repo update

# Install controller
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=YOUR_CLUSTER_NAME \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller
```

### 3. Verify kubectl Access

```bash
aws eks update-kubeconfig --region us-east-1 --name YOUR_CLUSTER_NAME
kubectl cluster-info
kubectl get nodes
```

---

## EKS Cluster Setup

### Create Cluster (if needed)

```bash
eksctl create cluster \
  --name lab-management-cluster \
  --region us-east-1 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 1 \
  --nodes-max 4 \
  --managed
```

---

## Kubernetes Deployment Walkthrough

### Manifest Descriptions

| File | Purpose |
|------|---------|
| `namespace.yaml` | Creates isolated `lab-management-system` namespace |
| `deployment.yaml` | Deploys 2 replicas with health probes, resource limits, env vars |
| `service.yaml` | ClusterIP service exposing port 80 → container 8080 |
| `ingress.yaml` | AWS ALB ingress with HTTPS redirect and health check config |

### Manual Deployment

```bash
# Apply in order
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
kubectl apply -f kubernetes/ingress.yaml

# Monitor rollout
kubectl rollout status deployment/lab-management-system -n lab-management-system

# Verify
kubectl get pods,svc,ingress -n lab-management-system
```

---

## Deploy Using Scripts

### Linux/macOS

```bash
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh
```

The script will prompt for:
- AWS Region
- EKS Cluster Name
- Docker Image URI
- Database connection details (DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD)
- API_BASE_URL, EQUIPMENT_DEFAULT_IP, EQUIPMENT_DEFAULT_PORT

### Windows

```cmd
scripts\deploy-image.bat
```

---

## Configuration Management

### Environment Variables Reference

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL hostname | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `labdb` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | _(empty)_ |
| `DB_URL` | Full JDBC URL | auto-built |
| `SERVER_PORT` | Application port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `docker` |
| `API_BASE_URL` | Base URL for API | `http://localhost:8080` |
| `LAB_DATA_PATH` | Lab data directory | `/app/data` |
| `LAB_BACKUP_PATH` | Backup directory | `/app/backup` |
| `LAB_REPORTS_PATH` | Reports directory | `/app/reports` |
| `EQUIPMENT_DEFAULT_IP` | Equipment IP | `192.168.1.100` |
| `EQUIPMENT_DEFAULT_PORT` | Equipment port | `8888` |
| `TZ` | Timezone | `UTC` |
| `JAVA_OPTS` | JVM options | See Dockerfile |

### Using Kubernetes Secrets for Sensitive Values

```bash
# Create secret for database credentials
kubectl create secret generic lab-db-secret \
  --from-literal=DB_PASSWORD=your-password \
  --from-literal=DB_USERNAME=postgres \
  -n lab-management-system
```

Then reference in deployment.yaml:
```yaml
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: lab-db-secret
      key: DB_PASSWORD
```

---

## EKS Scaling and Management

### Horizontal Pod Autoscaler

```bash
kubectl autoscale deployment lab-management-system \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n lab-management-system
```

### Rolling Update

```bash
# Update image
kubectl set image deployment/lab-management-system \
  lab-management-system=NEW_IMAGE_URI \
  -n lab-management-system

# Monitor
kubectl rollout status deployment/lab-management-system -n lab-management-system
```

### Rollback

```bash
kubectl rollout undo deployment/lab-management-system -n lab-management-system

# Rollback to specific revision
kubectl rollout undo deployment/lab-management-system \
  --to-revision=2 \
  -n lab-management-system
```

### Scale Manually

```bash
kubectl scale deployment lab-management-system --replicas=3 -n lab-management-system
```

---

## Troubleshooting

### Pod Not Starting

```bash
# Check pod status
kubectl get pods -n lab-management-system

# Describe pod for events
kubectl describe pod <pod-name> -n lab-management-system

# View logs
kubectl logs <pod-name> -n lab-management-system
kubectl logs <pod-name> -n lab-management-system --previous
```

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| `CrashLoopBackOff` | App startup failure | Check logs; verify DB connection |
| `ImagePullBackOff` | Cannot pull image | Verify ECR permissions and image URI |
| `Pending` pods | Insufficient resources | Scale node group or reduce resource requests |
| Health probe failing | App not ready | Increase `initialDelaySeconds` in probes |
| Ingress not resolving | ALB controller issue | Verify AWS Load Balancer Controller is installed |

### Database Connection Issues

```bash
# Exec into pod to test connectivity
kubectl exec -it <pod-name> -n lab-management-system -- sh

# Test DB connection (if nc available)
# nc -zv $DB_HOST $DB_PORT
```

### View Application Logs

```bash
# Stream logs
kubectl logs -f deployment/lab-management-system -n lab-management-system

# All pods
kubectl logs -l app=lab-management-system -n lab-management-system --all-containers
```

---

## Security Considerations

1. **Non-root container**: The Dockerfile runs the application as a non-root `appuser`
2. **Secrets management**: Use Kubernetes Secrets or AWS Secrets Manager for sensitive values
3. **Network policies**: Consider adding Kubernetes NetworkPolicy to restrict pod-to-pod traffic
4. **Image scanning**: Enable ECR image scanning for vulnerability detection
5. **HTTPS**: The ingress is configured with SSL redirect; ensure a valid ACM certificate is attached
6. **Resource limits**: CPU and memory limits are set to prevent resource exhaustion
7. **Read-only filesystem**: Consider adding `readOnlyRootFilesystem: true` with appropriate volume mounts

---

## Java-Specific Notes

### JVM Configuration

The application uses the following JVM flags (set via `JAVA_OPTS`):

```
-XX:+UseContainerSupport       # Respect container memory limits
-XX:MaxRAMPercentage=75.0      # Use 75% of container memory for heap
-Xms256m                       # Initial heap size
-Xmx512m                       # Maximum heap size
-Djava.security.egd=file:/dev/./urandom  # Faster random number generation
-Dfile.encoding=UTF-8
-Duser.timezone=UTC
```

### Spring Boot Profile

The `docker` Spring profile is activated via `SPRING_PROFILES_ACTIVE=docker`. Create `application-docker.properties` in `src/main/resources` to override settings for containerized deployments.

### Health Endpoint

The application exposes a custom health endpoint at `/health` (implemented in `HealthController`). This is used by both Kubernetes liveness and readiness probes.

### Startup Time

Spring Boot applications with JPA/Hibernate may take 30–60 seconds to start. The Kubernetes probes are configured with:
- `initialDelaySeconds: 60` for liveness
- `initialDelaySeconds: 30` for readiness

Adjust these values based on observed startup times in your environment.

### PostgreSQL

The application is configured to use PostgreSQL. Ensure a PostgreSQL instance is accessible from the EKS cluster before deploying. The connection is configured via environment variables:
- `DB_URL` or individual `DB_HOST`, `DB_PORT`, `DB_NAME`
- `DB_USERNAME`, `DB_PASSWORD`
