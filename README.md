# High-Performance Financial System Sample

## About

This is a **sample project** dedicated to demonstrating how a **high-performance financial system** can be implemented.

It consists of **three microservices**:

1. **Account Listing** – shows the list of accounts for a given client (by ID)
2. **Money Transfer** – transfers money from one account to another
3. **Fraud Detection** – exposes a single endpoint that always returns an `APPROVED` decision

---

## How to Start Guide

### Step 1: Create Docker Network

```bash
docker network create backend
```

### Step 2: Run Kafka

```cmd
docker run -d ^
  --name kafka ^
  -p 9092:9092 ^
  -e KAFKA_PROCESS_ROLES=broker,controller ^
  -e KAFKA_NODE_ID=1 ^
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093 ^
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://host.docker.internal:9092 ^
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT ^
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER ^
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 ^
  apache/kafka:latest
```

### Step 3: Run Fraud Detection Microservice

```bash
mvn clean package
docker build -t fraud-detection .
docker run --name fraud-detection --network backend -p 8082:8082 fraud-detection:latest
```

### Step 4: Run Money Transfer Microservice

```bash
mvn clean package
docker compose build
docker compose up
```

### Step 5: Run Account Listing Microservice

```bash
mvn clean package -DskipTests
docker compose build
docker compose up
```

---

## Explanations

### Account Listing

Reasons for separation:

- Mostly read-only
- Tolerates eventual consistency
- Benefits from Redis caching
This is why caching (Redis) have been used here.

Endpoint:

```http
GET http://localhost:8081/clients/{clientId}/accounts
```

Consumes Kafka `TransferCompletedEvent` with idempotency.

### Money Transfer

- Base URL: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- READ_COMMITTED isolation
- PESSIMISTIC_WRITE locking
- Idempotency have been used here to prevent double spending. 
- Circuit breaker have been used when calling "Fraud detection" external service with timeout of 80ms.
- Outbox design pattern have been used to send messages on TransferCompletedEvent to the Kafka in order to implement integration with other services like notification services, ledger updates, account listing etc.
- Retry + Circuit breaker have been used as an example of post processing and calling external services.

### Fraud Detection

Testing-only service.

- Swagger: http://localhost:8082/swagger-ui.html

---

## Performance Optimizations

### Database

```properties
spring.datasource.hikari.maximum-pool-size=50
```

### Indexes

```sql
CREATE INDEX idx_ledger_account ON ledger_entry(account_id);
CREATE INDEX idx_account_client_id ON account(client_id);
CREATE INDEX idx_outbox_event_status ON outbox_event(status);
CREATE INDEX idx_outbox_event_created_at ON outbox_event(created_at);
```

### JVM

```
-XX:+UseG1GC
-Xms2g -Xmx2g
```

### Spring

```properties
spring.jpa.open-in-view=false
```
