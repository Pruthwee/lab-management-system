# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy pom.xml and resolve dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the Spring Boot jar
COPY --from=builder /workspace/target/*.jar app.jar

# Set environment variables
ENV TZ=UTC
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV SPRING_PROFILES_ACTIVE=prod
ENV DATA_PATH=/app/data
ENV BACKUP_PATH=/app/backup
ENV REPORTS_PATH=/app/reports
ENV LOG_PATH=/app/logs

# Create directories for data
RUN mkdir -p /app/data /app/backup /app/reports /app/logs

# Expose the application port
EXPOSE 8080

# Run as non-root user
USER appuser

# Start the application with signal handling for graceful shutdown
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
