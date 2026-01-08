# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom and download dependencies first (cache-friendly)
COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Copy source
COPY src ./src

# Build jar
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Security: non-root user
RUN useradd -ms /bin/bash appuser
USER appuser

# Copy built jar
COPY --from=builder /app/target/accounts-list-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
