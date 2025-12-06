# Wiki Backend Microservices

A microservices-based wiki platform built with Spring Boot 3.4, Spring Cloud, and Keycloak authentication.

## Architecture

- **wiki-gateway**: API Gateway (Spring Cloud Gateway) - Port 8080
- **wiki-auth**: Authentication BFF service - Port 8081
- **wiki-organization**: Organization management service - Port 8082
- **wiki-common**: Shared library with common code

## Technology Stack

- Java 21
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
- Keycloak 24.0 (Identity Provider)
- PostgreSQL (for Keycloak and application data)
- MongoDB 7
- RabbitMQ 3
- Redis 7
- Kubernetes (Minikube)
- Skaffold

## Prerequisites

- JDK 21
- Gradle
- Docker
- Minikube
- Skaffold
- kubectl

## Local Development with Skaffold

```bash
# Start Minikube
minikube start

# Point Docker CLI to Minikube's Docker daemon
eval $(minikube docker-env)

# Deploy with Skaffold
skaffold dev

# Access services
# Gateway: http://localhost:8080
# Keycloak: http://localhost:8180
# RabbitMQ Management: http://localhost:15672
```

## Keycloak Setup

After deployment, configure Keycloak:

1. Access Keycloak at http://localhost:8180
2. Login with admin/admin
3. Create realm: `wiki`
4. Configure clients and roles as needed

## Project Structure

```
wiki-backend/
├── wiki-common/         # Shared library
├── wiki-gateway/        # API Gateway
├── wiki-auth/          # Auth BFF
├── wiki-organization/  # Organization service
└── k8s/               # Kubernetes manifests
```

## API Endpoints

- `GET /api/organizations/**` - Organization management
- `POST /api/auth/**` - Authentication

## Database Migrations

Flyway migrations are located in `wiki-organization/src/main/resources/db/migration/`

## Contributing

1. Create feature branch
2. Make changes
3. Test locally with Skaffold
4. Submit PR
