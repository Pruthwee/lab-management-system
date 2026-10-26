#!/bin/bash
set -e
set -o pipefail

PROJECT_NAME="lab-management-system"
IMAGE_NAME=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')
SERVICE_NAME="${IMAGE_NAME}-service"
CLUSTER_NAME="${IMAGE_NAME}-cluster"
TASK_FAMILY="${IMAGE_NAME}-task"
LOG_GROUP="/ecs/${IMAGE_NAME}"

echo "=== Deploy Script for $IMAGE_NAME on AWS ECS Fargate ==="
echo ""

# Ask for AWS region
read -p "Enter AWS region (e.g., us-east-1): " AWS_REGION

# Get account ID
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "AWS Account ID: $ACCOUNT_ID"

# Ask for image URI
read -p "Enter the full image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/app:latest): " IMAGE_URI

# Ask for cluster name
read -p "Enter ECS cluster name (default: $CLUSTER_NAME): " INPUT_CLUSTER
if [ -n "$INPUT_CLUSTER" ]; then
    CLUSTER_NAME="$INPUT_CLUSTER"
fi

# Ask for VPC ID
read -p "Enter VPC ID: " VPC_ID

# Ask for subnet IDs
read -p "Enter subnet IDs (comma-separated, at least two): " SUBNET_INPUT
# Convert comma-separated to JSON array format
SUBNET_1=$(echo "$SUBNET_INPUT" | cut -d',' -f1 | tr -d ' ')
SUBNET_2=$(echo "$SUBNET_INPUT" | cut -d',' -f2 | tr -d ' ')

# Ask for security group ID
read -p "Enter security group ID (must allow inbound on port 8080): " SECURITY_GROUP

# Ask about load balancer
read -p "Do you need a load balancer for this service? (y/n): " LB_CHOICE

# Create cluster if missing
echo ""
echo "=== Checking/Creating ECS cluster ==="
aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$AWS_REGION" 2>/dev/null || \
    aws ecs create-cluster --cluster-name "$CLUSTER_NAME" --region "$AWS_REGION"

# Create log group if missing
echo ""
echo "=== Checking/Creating CloudWatch log group ==="
EXISTING_LOG_GROUP=$(aws logs describe-log-groups --log-group-name-prefix "$LOG_GROUP" --region "$AWS_REGION" --query 'logGroups[0].logGroupName' --output text 2>/dev/null)
if [ "$EXISTING_LOG_GROUP" != "$LOG_GROUP" ]; then
    aws logs create-log-group --log-group-name "$LOG_GROUP" --region "$AWS_REGION"
    echo "Created log group: $LOG_GROUP"
else
    echo "Log group already exists: $LOG_GROUP"
fi

# Handle load balancer
TARGET_GROUP_ARN=""
SERVICE_JSON_FILE="$JOB_WORK_DIR/service-definition.json"
if [ "$LB_CHOICE" = "y" ] || [ "$LB_CHOICE" = "Y" ]; then
    echo ""
    echo "=== Creating Application Load Balancer ==="
    LB_NAME="${IMAGE_NAME}-alb"
    TG_NAME="${IMAGE_NAME}-tg"

    # Create security group for ALB if needed
    ALB_SG=$(aws ec2 create-security-group --group-name "${IMAGE_NAME}-alb-sg" --description "ALB security group for $IMAGE_NAME" --vpc-id "$VPC_ID" --region "$AWS_REGION" --query 'GroupId' --output text 2>/dev/null || echo "")

    # Create ALB
    ALB_ARN=$(aws elbv2 create-load-balancer \
        --name "$LB_NAME" \
        --subnets "$SUBNET_1" "$SUBNET_2" \
        --security-groups "$SECURITY_GROUP" \
        --scheme internet-facing \
        --type application \
        --region "$AWS_REGION" \
        --query 'LoadBalancers[0].LoadBalancerArn' \
        --output text)

    # Create target group
    TG_ARN=$(aws elbv2 create-target-group \
        --name "$TG_NAME" \
        --protocol HTTP \
        --port 8080 \
        --vpc-id "$VPC_ID" \
        --target-type ip \
        --health-check-path "/health" \
        --health-check-port "8080" \
        --region "$AWS_REGION" \
        --query 'TargetGroups[0].TargetGroupArn' \
        --output text)

    # Create listener
    aws elbv2 create-listener \
        --load-balancer-arn "$ALB_ARN" \
        --protocol HTTP \
        --port 80 \
        --default-actions "Type=forward,TargetGroupArn=$TG_ARN" \
        --region "$AWS_REGION" >/dev/null

    TARGET_GROUP_ARN="$TG_ARN"
    echo "ALB created: $ALB_ARN"
    echo "Target group created: $TG_ARN"
else
    echo ""
    echo "=== No load balancer requested ==="
fi

# Handle credentials - ask for each one
echo ""
echo "=== Setting up credentials in AWS Secrets Manager ==="

# DB_URL
read -p "Enter DB_URL (e.g., jdbc:postgresql://host:5432/db): " DB_URL_VALUE
DB_URL_SECRET_ARN=$(aws secretsmanager create-secret --name "${IMAGE_NAME}/DB_URL" --description "DB_URL for $IMAGE_NAME" --secret-string "$DB_URL_VALUE" --region "$AWS_REGION" --query 'ARN' --output text 2>/dev/null || \
    aws secretsmanager put-secret-value --secret-id "${IMAGE_NAME}/DB_URL" --secret-string "$DB_URL_VALUE" --region "$AWS_REGION" --query 'ARN' --output text)

# DB_USERNAME
read -p "Enter DB_USERNAME: " DB_USERNAME_VALUE
DB_USERNAME_SECRET_ARN=$(aws secretsmanager create-secret --name "${IMAGE_NAME}/DB_USERNAME" --description "DB_USERNAME for $IMAGE_NAME" --secret-string "$DB_USERNAME_VALUE" --region "$AWS_REGION" --query 'ARN' --output text 2>/dev/null || \
    aws secretsmanager put-secret-value --secret-id "${IMAGE_NAME}/DB_USERNAME" --secret-string "$DB_USERNAME_VALUE" --region "$AWS_REGION" --query 'ARN' --output text)

# DB_PASSWORD
read -s -p "Enter DB_PASSWORD: " DB_PASSWORD_VALUE
echo ""
DB_PASSWORD_SECRET_ARN=$(aws secretsmanager create-secret --name "${IMAGE_NAME}/DB_PASSWORD" --description "DB_PASSWORD for $IMAGE_NAME" --secret-string "$DB_PASSWORD_VALUE" --region "$AWS_REGION" --query 'ARN' --output text 2>/dev/null || \
    aws secretsmanager put-secret-value --secret-id "${IMAGE_NAME}/DB_PASSWORD" --secret-string "$DB_PASSWORD_VALUE" --region "$AWS_REGION" --query 'ARN' --output text)

# Prepare task definition
echo ""
echo "=== Registering task definition ==="
TASK_FILE="ecs/task-definition.json"
TMP_TASK_FILE="$JOB_WORK_DIR/task-definition-filled.json"

# Replace placeholders using sed with pipe delimiter
sed \
    -e "s|{{ACCOUNT_ID}}|$ACCOUNT_ID|g" \
    -e "s|{{AWS_REGION}}|$AWS_REGION|g" \
    -e "s|{{IMAGE_URI}}|$IMAGE_URI|g" \
    -e "s|{{DB_URL_SECRET_ARN}}|$DB_URL_SECRET_ARN|g" \
    -e "s|{{DB_USERNAME_SECRET_ARN}}|$DB_USERNAME_SECRET_ARN|g" \
    -e "s|{{DB_PASSWORD_SECRET_ARN}}|$DB_PASSWORD_SECRET_ARN|g" \
    "$TASK_FILE" > "$TMP_TASK_FILE"

# Register task definition and get the full ARN
TASK_DEF_ARN=$(aws ecs register-task-definition --cli-input-json file://"$TMP_TASK_FILE" --region "$AWS_REGION" --query 'taskDefinition.taskDefinitionArn' --output text)
echo "Task definition registered: $TASK_DEF_ARN"

# Prepare service definition
SERVICE_FILE="ecs/service-definition.json"
TMP_SERVICE_FILE="$JOB_WORK_DIR/service-definition-filled.json"

if [ "$LB_CHOICE" = "y" ] || [ "$LB_CHOICE" = "Y" ]; then
    sed \
        -e "s|{{CLUSTER_NAME}}|$CLUSTER_NAME|g" \
        -e "s|{{TARGET_GROUP_ARN}}|$TARGET_GROUP_ARN|g" \
        -e "s|{{SUBNET_1}}|$SUBNET_1|g" \
        -e "s|{{SUBNET_2}}|$SUBNET_2|g" \
        -e "s|{{SECURITY_GROUP}}|$SECURITY_GROUP|g" \
        "$SERVICE_FILE" > "$TMP_SERVICE_FILE"
else
    # Remove the loadBalancers section
    python3 -c "
import json
with open('$SERVICE_FILE') as f:
    svc = json.load(f)
svc.pop('loadBalancers', None)
svc.pop('healthCheckGracePeriodSeconds', None)
svc['cluster'] = '$CLUSTER_NAME'
svc['taskDefinition'] = '$TASK_FAMILY'
with open('$TMP_SERVICE_FILE', 'w') as f:
    json.dump(svc, f, indent=2)
"
fi

# Check if service exists
echo ""
echo "=== Checking/Creating ECS service ==="
EXISTING_SERVICE=$(aws ecs describe-services --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$AWS_REGION" --query 'services[0].serviceName' --output text 2>/dev/null)

if [ "$EXISTING_SERVICE" = "$SERVICE_NAME" ]; then
    echo "Service exists, updating..."
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "$SERVICE_NAME" \
        --task-definition "$TASK_DEF_ARN" \
        --desired-count 2 \
        --region "$AWS_REGION"
else
    echo "Creating new service..."
    aws ecs create-service \
        --cli-input-json file://"$TMP_SERVICE_FILE" \
        --region "$AWS_REGION" \
        --task-definition "$TASK_DEF_ARN"
fi

# Wait for service to stabilize
echo ""
echo "=== Waiting for service to stabilize ==="
aws ecs wait services-stable --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$AWS_REGION"

# Show service details
echo ""
echo "=== Service Details ==="
aws ecs describe-services --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$AWS_REGION"

if [ "$LB_CHOICE" = "y" ] || [ "$LB_CHOICE" = "Y" ]; then
    echo ""
    echo "=== Load Balancer DNS ==="
    aws elbv2 describe-load-balancers --load-balancer-arns "$ALB_ARN" --region "$AWS_REGION" --query 'LoadBalancers[0].DNSName' --output text
fi

echo ""
echo "=== Log Group ==="
echo "CloudWatch log group: $LOG_GROUP"

echo ""
echo "=== Troubleshooting Hints ==="
echo "1. If tasks fail to start: check CloudWatch logs in $LOG_GROUP"
echo "2. If tasks fail with 'CannotPullImage': ensure ecsTaskExecutionRole has ECR pull permissions"
echo "3. If tasks fail with 'ResourceInitializationError': check EFS mount target security group allows NFS (port 2049)"
echo "4. If health checks fail: ensure the application's /health endpoint responds within the health check grace period"
echo "5. If secrets fail to load: ensure ecsTaskExecutionRole has secretsmanager:GetSecretValue permission"
echo "6. If port issues: ensure the security group allows inbound traffic on port 8080"
