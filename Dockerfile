# =============================================================================
# Other Tales Backend - Optimized Dockerfile for Google Cloud Run
# Strategy: Containerized Build (No Maven Wrapper required)
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Build (Maven official image)
# -----------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# 1. Copy pom.xml first (layer caching for dependencies)
COPY pom.xml .

# 2. Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# 3. Copy source code
COPY src/ src/

# 4. Build the application (skip tests - validated in CI)
RUN mvn clean package -DskipTests -B

# -----------------------------------------------------------------------------
# Stage 2: Runtime (Lightweight JRE image)
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Security: Run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

USER appuser

# Cloud Run uses PORT env variable (default 8080)
ENV PORT=8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Optimized JVM flags for containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
