@echo off
setlocal enabledelayedexpansion

set "PROJECT_NAME=lab-management-system"
set "IMAGE_NAME=%PROJECT_NAME%"
set "SERVICE_NAME=%IMAGE_NAME%-service"
set "CLUSTER_NAME=%IMAGE_NAME%-cluster"
set "TASK_FAMILY=%IMAGE_NAME%-task"
set "LOG_GROUP=/ecs/%IMAGE_NAME%"

echo === Deploy Script for %IMAGE_NAME% on AWS ECS Fargate ===
echo.

set /p AWS_REGION="Enter AWS region (e.g., us-east-1): "

echo Getting AWS Account ID...
for /f "delims=" %%i in ('aws sts get-caller-identity --query Account --output text') do set "ACCOUNT_ID=%%i"
echo AWS Account ID: !ACCOUNT_ID!

set /p IMAGE_URI="Enter the full image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/app:latest): "

set /p INPUT_CLUSTER="Enter ECS cluster name (default: %CLUSTER_NAME%): "
if not "!INPUT_CLUSTER!"=="" (
    set "CLUSTER_NAME=!INPUT_CLUSTER!"
)

set /p VPC_ID="Enter VPC ID: "

set /p SUBNET_INPUT="Enter subnet IDs (comma-separated, at least two): "
for /f "tokens=1,2 delims=," %%a in ("!SUBNET_INPUT!") do (
    set "SUBNET_1=%%a"
    set "SUBNET_2=%%b"
)

set /p SECURITY_GROUP="Enter security group ID (must allow inbound on port 8080): "

set /p LB_CHOICE="Do you need a load balancer for this service? (y/n): "

echo.
echo === Checking/Creating ECS cluster ===
aws ecs describe-clusters --clusters !CLUSTER_NAME! --region !AWS_REGION! 2>nul
if !ERRORLEVEL! neq 0 (
    aws ecs create-cluster --cluster-name !CLUSTER_NAME! --region !AWS_REGION!
)

echo.
echo === Checking/Creating CloudWatch log group ===
for /f "delims=" %%i in ('aws logs describe-log-groups --log-group-name-prefix !LOG_GROUP! --region !AWS_REGION! --query "logGroups[0].logGroupName" --output text 2^>nul') do set "EXISTING_LOG_GROUP=%%i"
if not "!EXISTING_LOG_GROUP!"=="!LOG_GROUP!" (
    aws logs create-log-group --log-group-name !LOG_GROUP! --region !AWS_REGION!
    echo Created log group: !LOG_GROUP!
) else (
    echo Log group already exists: !LOG_GROUP!
)

set "TARGET_GROUP_ARN="
set "TMP_SERVICE_FILE=%JOB_WORK_DIR%\service-definition-filled.json"

if /i "!LB_CHOICE!"=="y" (
    echo.
    echo === Creating Application Load Balancer ===
    set "LB_NAME=%IMAGE_NAME%-alb"
    set "TG_NAME=%IMAGE_NAME%-tg"

    for /f "delims=" %%i in ('aws ec2 create-security-group --group-name "%IMAGE_NAME%-alb-sg" --description "ALB security group for %IMAGE_NAME%" --vpc-id !VPC_ID! --region !AWS_REGION! --query "GroupId" --output text 2^>nul') do set "ALB_SG=%%i"

    for /f "delims=" %%i in ('aws elbv2 create-load-balancer --name !LB_NAME! --subnets !SUBNET_1! !SUBNET_2! --security-groups !SECURITY_GROUP! --scheme internet-facing --type application --region !AWS_REGION! --query "LoadBalancers[0].LoadBalancerArn" --output text') do set "ALB_ARN=%%i"

    for /f "delims=" %%i in ('aws elbv2 create-target-group --name !TG_NAME! --protocol HTTP --port 8080 --vpc-id !VPC_ID! --target-type ip --health-check-path "/health" --health-check-port "8080" --region !AWS_REGION! --query "TargetGroups[0].TargetGroupArn" --output text') do set "TG_ARN=%%i"

    aws elbv2 create-listener --load-balancer-arn !ALB_ARN! --protocol HTTP --port 80 --default-actions "Type=forward,TargetGroupArn=!TG_ARN!" --region !AWS_REGION! >nul

    set "TARGET_GROUP_ARN=!TG_ARN!"
    echo ALB created: !ALB_ARN!
    echo Target group created: !TG_ARN!
) else (
    echo.
    echo === No load balancer requested ===
)

echo.
echo === Setting up credentials in AWS Secrets Manager ===

set /p DB_URL_VALUE="Enter DB_URL (e.g., jdbc:postgresql://host:5432/db): "
for /f "delims=" %%i in ('aws secretsmanager create-secret --name "%IMAGE_NAME%/DB_URL" --description "DB_URL for %IMAGE_NAME%" --secret-string "!DB_URL_VALUE!" --region !AWS_REGION! --query "ARN" --output text 2^>nul') do set "DB_URL_SECRET_ARN=%%i"
if "!DB_URL_SECRET_ARN!"=="" (
    for /f "delims=" %%i in ('aws secretsmanager put-secret-value --secret-id "%IMAGE_NAME%/DB_URL" --secret-string "!DB_URL_VALUE!" --region !AWS_REGION! --query "ARN" --output text') do set "DB_URL_SECRET_ARN=%%i"
)

set /p DB_USERNAME_VALUE="Enter DB_USERNAME: "
for /f "delims=" %%i in ('aws secretsmanager create-secret --name "%IMAGE_NAME%/DB_USERNAME" --description "DB_USERNAME for %IMAGE_NAME%" --secret-string "!DB_USERNAME_VALUE!" --region !AWS_REGION! --query "ARN" --output text 2^>nul') do set "DB_USERNAME_SECRET_ARN=%%i"
if "!DB_USERNAME_SECRET_ARN!"=="" (
    for /f "delims=" %%i in ('aws secretsmanager put-secret-value --secret-id "%IMAGE_NAME%/DB_USERNAME" --secret-string "!DB_USERNAME_VALUE!" --region !AWS_REGION! --query "ARN" --output text') do set "DB_USERNAME_SECRET_ARN=%%i"
)

set /p DB_PASSWORD_VALUE="Enter DB_PASSWORD: "
for /f "delims=" %%i in ('aws secretsmanager create-secret --name "%IMAGE_NAME%/DB_PASSWORD" --description "DB_PASSWORD for %IMAGE_NAME%" --secret-string "!DB_PASSWORD_VALUE!" --region !AWS_REGION! --query "ARN" --output text 2^>nul') do set "DB_PASSWORD_SECRET_ARN=%%i"
if "!DB_PASSWORD_SECRET_ARN!"=="" (
    for /f "delims=" %%i in ('aws secretsmanager put-secret-value --secret-id "%IMAGE_NAME%/DB_PASSWORD" --secret-string "!DB_PASSWORD_VALUE!" --region !AWS_REGION! --query "ARN" --output text') do set "DB_PASSWORD_SECRET_ARN=%%i"
)

echo.
echo === Registering task definition ===
set "TASK_FILE=ecs\task-definition.json"
set "TMP_TASK_FILE=%JOB_WORK_DIR%\task-definition-filled.json"

powershell -Command "(Get-Content '%TASK_FILE%') -replace '{{ACCOUNT_ID}}', '!ACCOUNT_ID!' -replace '{{AWS_REGION}}', '!AWS_REGION!' -replace '{{IMAGE_URI}}', '!IMAGE_URI!' -replace '{{DB_URL_SECRET_ARN}}', '!DB_URL_SECRET_ARN!' -replace '{{DB_USERNAME_SECRET_ARN}}', '!DB_USERNAME_SECRET_ARN!' -replace '{{DB_PASSWORD_SECRET_ARN}}', '!DB_PASSWORD_SECRET_ARN!' | Set-Content '%TMP_TASK_FILE%'"

for /f "delims=" %%i in ('aws ecs register-task-definition --cli-input-json file://"%TMP_TASK_FILE%" --region !AWS_REGION! --query "taskDefinition.taskDefinitionArn" --output text') do set "TASK_DEF_ARN=%%i"
echo Task definition registered: !TASK_DEF_ARN!

echo.
echo === Checking/Creating ECS service ===
for /f "delims=" %%i in ('aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[0].serviceName" --output text 2^>nul') do set "EXISTING_SERVICE=%%i"

if "!EXISTING_SERVICE!"=="!SERVICE_NAME!" (
    echo Service exists, updating...
    aws ecs update-service --cluster !CLUSTER_NAME! --service !SERVICE_NAME! --task-definition !TASK_DEF_ARN! --desired-count 2 --region !AWS_REGION!
) else (
    echo Creating new service...
    if /i "!LB_CHOICE!"=="y" (
        powershell -Command "(Get-Content 'ecs\service-definition.json') -replace '{{CLUSTER_NAME}}', '!CLUSTER_NAME!' -replace '{{TARGET_GROUP_ARN}}', '!TARGET_GROUP_ARN!' -replace '{{SUBNET_1}}', '!SUBNET_1!' -replace '{{SUBNET_2}}', '!SUBNET_2!' -replace '{{SECURITY_GROUP}}', '!SECURITY_GROUP!' | Set-Content '!TMP_SERVICE_FILE!'"
    ) else (
        powershell -Command "$svc = Get-Content 'ecs\service-definition.json' | ConvertFrom-Json; $svc.PSObject.Properties.Remove('loadBalancers'); $svc.PSObject.Properties.Remove('healthCheckGracePeriodSeconds'); $svc.cluster = '!CLUSTER_NAME!'; $svc.taskDefinition = '!TASK_FAMILY!'; $svc | ConvertTo-Json -Depth 10 | Set-Content '!TMP_SERVICE_FILE!'"
    )
    aws ecs create-service --cli-input-json file://"!TMP_SERVICE_FILE!" --region !AWS_REGION! --task-definition !TASK_DEF_ARN!
)

echo.
echo === Waiting for service to stabilize ===
aws ecs wait services-stable --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION!

echo.
echo === Service Details ===
aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION!

if /i "!LB_CHOICE!"=="y" (
    echo.
    echo === Load Balancer DNS ===
    for /f "delims=" %%i in ('aws elbv2 describe-load-balancers --load-balancer-arns !ALB_ARN! --region !AWS_REGION! --query "LoadBalancers[0].DNSName" --output text') do set "LB_DNS=%%i"
    echo Load Balancer DNS: !LB_DNS!
)

echo.
echo === Log Group ===
echo CloudWatch log group: !LOG_GROUP!

echo.
echo === Troubleshooting Hints ===
echo 1. If tasks fail to start: check CloudWatch logs in !LOG_GROUP!
echo 2. If tasks fail with 'CannotPullImage': ensure ecsTaskExecutionRole has ECR pull permissions
echo 3. If tasks fail with 'ResourceInitializationError': check EFS mount target security group allows NFS (port 2049)
echo 4. If health checks fail: ensure the application's /health endpoint responds within the health check grace period
echo 5. If secrets fail to load: ensure ecsTaskExecutionRole has secretsmanager:GetSecretValue permission
echo 6. If port issues: ensure the security group allows inbound traffic on port 8080

endlocal
