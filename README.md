# Axis Backend Microservices

A microservices-based life goals planning platform (similar to Trello) built with Spring Boot 3.4, Spring Cloud, and Keycloak authentication. Axis helps users organize and track their long-term, medium-term, and short-term life goals using an intuitive board-based interface.

## Architecture

- **axis-gateway**: API Gateway (Spring Cloud Gateway) - Port 8080
- **axis-media**: Media management service - Port 8083
- **axis-common**: Shared library with common code

## Core Features

### Goal Management System
- **Long-term goals**: Strategic life objectives (1-5+ years)
- **Medium-term goals**: Quarterly/yearly milestones (3-12 months)
- **Short-term goals**: Daily/weekly tasks (days to weeks)

### Board System (Trello-like)
- Create multiple boards for different life areas (career, health, relationships, etc.)
- Organize goals into customizable columns
- Drag-and-drop task management
- Progress tracking and visualization
- Collaborative goal setting

## Technology Stack

- Java 21
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
- Keycloak 24.0 (Identity Provider)
- PostgreSQL (for Keycloak and future application data)
- MongoDB 7 (for media storage)
- RabbitMQ 3 (message broker)
- Redis 7 (caching)
- Kubernetes (Minikube)
- Skaffold (local development)

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

# Deploy with Skaffold (dev mode with auto-reload)
skaffold dev

# Access services
# Gateway: http://localhost:8080
# Keycloak: http://localhost:8180
# RabbitMQ Management: http://localhost:15672
```

## Keycloak Setup

After deployment, Keycloak realm 'axis' is automatically configured with:

1. Access Keycloak at http://localhost:8180
2. Realm: `axis`
3. Client: `axis-backend` (secret: `secret`)
4. Test users:
   - `admin` / `admin` (roles: admin, user)
   - `user` / `user` (role: user)

## Project Structure

```
axis-backend/
├── axis-common/         # Shared library (security, DTOs, exceptions)
├── axis-gateway/        # API Gateway (Spring Cloud Gateway)
├── axis-media/          # Media management service
└── k8s/                 # Kubernetes manifests
    ├── config/          # ConfigMaps and Secrets
    ├── infrastructure/  # Keycloak, PostgreSQL, MongoDB, RabbitMQ, Redis
    └── services/        # Microservice deployments
```

## API Endpoints

- `GET /api/media/**` - Media upload/management
- `GET /actuator/health` - Health checks

## Database Migrations

Future Flyway migrations will be located in service-specific `src/main/resources/db/migration/` directories.

## Building

```bash
# Build all services
./gradlew clean build

# Build specific service
./gradlew :axis-media:build

# Build Docker images (uses Jib)
./gradlew jibDockerBuild
```

## Contributing

1. Create feature branch
2. Make changes
3. Test locally with Skaffold
4. Submit PR

## Migration from Wiki Platform

This project was migrated from wiki-backend to focus on life goals planning. See [MIGRATION_TO_AXIS.md](MIGRATION_TO_AXIS.md) for migration details.

## License

Proprietary - All rights reserved
