#!/bin/bash
set -e
set -o pipefail

# ============================================================
# deploy-image.sh - Deploy Lab Management System to AWS EKS
# ============================================================

APP_NAME="lab-management-system"
NAMESPACE="lab-management-system"
K8S_DIR="kubernetes"

echo "============================================================"
echo "  Lab Management System - AWS EKS Deployment"
echo "============================================================"

# Prompt for AWS configuration
echo ""
read -p "Enter AWS Region (e.g. us-east-1): " AWS_REGION
if [ -z "$AWS_REGION" ]; then
  echo "ERROR: AWS Region is required."
  exit 1
fi

read -p "Enter EKS Cluster Name: " CLUSTER_NAME
if [ -z "$CLUSTER_NAME" ]; then
  echo "ERROR: EKS Cluster Name is required."
  exit 1
fi

read -p "Enter full Docker Image URI (e.g. 123456789.dkr.ecr.us-east-1.amazonaws.com/lab-management-system:latest): " IMAGE_URI
if [ -z "$IMAGE_URI" ]; then
  echo "ERROR: Docker Image URI is required."
  exit 1
fi

# Prompt for application-specific environment variables
echo ""
echo "------------------------------------------------------------"
echo "  Application Environment Variables (press Enter to skip)"
echo "------------------------------------------------------------"

read -p "Enter DB_HOST (database hostname): " DB_HOST
read -p "Enter DB_PORT (default: 5432): " DB_PORT
read -p "Enter DB_NAME (default: labdb): " DB_NAME
read -p "Enter DB_USERNAME (default: postgres): " DB_USERNAME
read -sp "Enter DB_PASSWORD: " DB_PASSWORD
echo ""
read -p "Enter DB_URL (or leave blank to auto-build): " DB_URL
read -p "Enter API_BASE_URL (e.g. https://lab-management-system.example.com): " API_BASE_URL
read -p "Enter EQUIPMENT_DEFAULT_IP (default: 192.168.1.100): " EQUIPMENT_DEFAULT_IP
read -p "Enter EQUIPMENT_DEFAULT_PORT (default: 8888): " EQUIPMENT_DEFAULT_PORT

# Set defaults
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-labdb}"
DB_USERNAME="${DB_USERNAME:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_URL="${DB_URL:-jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}}"
API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"
EQUIPMENT_DEFAULT_IP="${EQUIPMENT_DEFAULT_IP:-192.168.1.100}"
EQUIPMENT_DEFAULT_PORT="${EQUIPMENT_DEFAULT_PORT:-8888}"

echo ""
echo "------------------------------------------------------------"
echo "  Configuring kubectl for EKS cluster: $CLUSTER_NAME"
echo "------------------------------------------------------------"
aws eks update-kubeconfig --region "$AWS_REGION" --name "$CLUSTER_NAME"

echo "Verifying cluster connectivity..."
kubectl cluster-info || { echo "ERROR: Cannot connect to cluster."; exit 1; }

echo ""
echo "------------------------------------------------------------"
echo "  Updating Kubernetes manifests with deployment values"
echo "------------------------------------------------------------"

# Replace IMAGE_URI placeholder
sed -i 's|{{IMAGE_URI}}|'"$IMAGE_URI"'|g' "$K8S_DIR/deployment.yaml"

# Replace environment variable placeholders
sed -i 's|{{DB_HOST}}|'"$DB_HOST"'|g' "$K8S_DIR/deployment.yaml"
sed -i 's|{{DB_PORT}}|'"$DB_PORT"'|g' "$K8S_DIR/deployment.yaml"
sed -i 's|{{DB_NAME}}|'"$DB_NAME"'|g' "$K8S_DIR/deployment.yaml"
sed -i 's|{{DB_USERNAME}}|'"$DB_USERNAME"'|g' "$K8S_DIR/deployment.yaml"
sed -i 's|{{DB_PASSWORD}}|'"$DB_PASSWORD"'|g' "$K8S_DIR/deployment.yaml"
sed -i 's|{{DB_URL}}|'"$DB_URL"'|g' "$K8S_DIR/deployment.yaml"
sed -i 's|{{API_BASE_URL}}|'"$API_BASE_URL"'|g' "$K8S_DIR/deployment.yaml"
sed -i 's|{{EQUIPMENT_DEFAULT_IP}}|'"$EQUIPMENT_DEFAULT_IP"'|g' "$K8S_DIR/deployment.yaml"
sed -i 's|{{EQUIPMENT_DEFAULT_PORT}}|'"$EQUIPMENT_DEFAULT_PORT"'|g' "$K8S_DIR/deployment.yaml"

echo ""
echo "------------------------------------------------------------"
echo "  Applying Kubernetes manifests"
echo "------------------------------------------------------------"

echo "Applying namespace..."
kubectl apply -f "$K8S_DIR/namespace.yaml"

echo "Applying deployment..."
kubectl apply -f "$K8S_DIR/deployment.yaml"

echo "Applying service..."
kubectl apply -f "$K8S_DIR/service.yaml"

echo "Applying ingress..."
kubectl apply -f "$K8S_DIR/ingress.yaml"

echo ""
echo "------------------------------------------------------------"
echo "  Waiting for deployment rollout..."
echo "------------------------------------------------------------"
kubectl rollout status deployment/"$APP_NAME" -n "$NAMESPACE" --timeout=300s

echo ""
echo "------------------------------------------------------------"
echo "  Verifying deployed resources"
echo "------------------------------------------------------------"
kubectl get pods,svc,ingress -n "$NAMESPACE"

echo ""
echo "------------------------------------------------------------"
echo "  Application Access"
echo "------------------------------------------------------------"
INGRESS_HOST=$(kubectl get ingress "$APP_NAME-ingress" -n "$NAMESPACE" -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || echo "pending")
echo "  Ingress Hostname : $INGRESS_HOST"
echo "  Health Check     : http://$INGRESS_HOST/health"
echo ""
echo "  NOTE: DNS propagation may take a few minutes."
echo ""
echo "  Rollback command (if needed):"
echo "    kubectl rollout undo deployment/$APP_NAME -n $NAMESPACE"
echo ""
echo "============================================================"
echo "  Deployment complete!"
echo "============================================================"
