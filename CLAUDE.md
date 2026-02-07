# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Axis Backend is a microservices-based life goals planning platform (similar to Trello) built with Spring Boot 3.4, Spring Cloud, and Keycloak authentication. The system helps users organize and track long-term, medium-term, and short-term life goals using a board-based interface. The platform runs on Kubernetes (Minikube) and uses Skaffold for local development.

### Microservices

- **axis-gateway** (Port 8080): Spring Cloud Gateway, WebFlux-based reactive API gateway with OAuth2 JWT resource server
- **axis-goal** (Port 8081): Goals management service with PostgreSQL storage for long/medium/short-term goals, goal types, and custom fields
- **axis-notification** (Port 8082): Notification management service with PostgreSQL storage for notification logs, user settings, and templates
- **axis-media** (Port 8083): Media file management service with MongoDB storage
- **axis-common**: Shared library containing security utilities, exception handling, and DTOs

### Technology Stack

- Java 21 (use modern features: records, pattern matching, sealed classes)
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
- GraalVM Native Image (for CI/CD production builds)
- Keycloak 24.0 (realm: `axis`, client: `axis-backend`)
- PostgreSQL (separate instances for Keycloak and future application data)
- MongoDB 7
- RabbitMQ 3
- Redis 7
- Kubernetes (Minikube) with Skaffold

## Domain Model

### Goals System (Future Implementation)
- **Long-term Goals**: Strategic objectives (1-5+ years)
- **Medium-term Goals**: Quarterly/yearly milestones (3-12 months)
- **Short-term Goals**: Daily/weekly tasks (days to weeks)

### Board System (Future Implementation)
- Trello-like boards for organizing goals
- Customizable columns/lists
- Card-based goal representation
- Progress tracking

## Build and Development Commands

### Hybrid Build Strategy

The project uses a **hybrid build approach** optimized for both development speed and production performance:

- **Local Development (JVM)**: Fast iteration with Skaffold, quick rebuilds, hot-reload support
- **CI/CD Production (Native)**: GraalVM native images for faster startup, lower memory, and smaller containers

**Rationale:**
- Native compilation takes 5-10x longer than JVM builds
- Local dev benefits from JIT optimization for long-running services
- Production benefits from instant startup and reduced resource usage
- Best of both worlds: fast iteration + optimal deployment

### Building (JVM - Local Development)

```bash
# Build all services (JVM)
./gradlew clean build

# Build specific service (JVM)
./gradlew :axis-media:build

# Build Docker images (uses Jib for JVM-based images)
./gradlew jibDockerBuild
```

### Building Native Images (CI/CD Production Only)

**IMPORTANT:** This project uses Spring Boot buildpacks exclusively for native image compilation. The `org.graalvm.buildtools.native` Gradle plugin is NOT used and should NOT be added to service build.gradle files, as it interferes with Spring AOT processing.

```bash
# Build native Docker image using Spring Boot buildpacks
# This is done automatically in CI/CD (GitHub Actions)
./gradlew :axis-gateway:bootBuildImage
./gradlew :axis-goal:bootBuildImage
./gradlew :axis-notification:bootBuildImage
./gradlew :axis-media:bootBuildImage
```

**Key Points:**
- Native builds take 5-10 minutes per service
- Spring Boot buildpacks handle all native image configuration
- Spring AOT processing is automatic (no GraalVM plugin needed)
- Use JVM builds for local development
- Let CI/CD handle native image builds

### Local Development with Skaffold

Skaffold uses **Kustomize overlays** to support both JVM and native image deployments with optimized resource configurations.

```bash
# Start Minikube (if not running)
minikube start

# Point Docker CLI to Minikube's Docker daemon
eval $(minikube docker-env)

# Deploy with JVM images (default, fast builds, hot-reload)
skaffold dev

# Deploy JVM images without watching
skaffold run

# Deploy with native images (slow builds, optimized runtime)
skaffold run -p native

# Delete deployment
skaffold delete
```

### Kustomize Structure

The project uses Kustomize overlays for different deployment scenarios:

```
k8s/
├── base/                       # Base manifests (all services and infrastructure)
│   ├── kustomization.yaml
│   ├── namespace.yaml
│   ├── axis-gateway.yaml       # Service deployments
│   ├── axis-goal.yaml
│   ├── axis-media.yaml
│   ├── axis-notification.yaml
│   ├── infrastructure/         # Keycloak, PostgreSQL, MongoDB, RabbitMQ, Redis
│   └── config/                 # ConfigMaps and Secrets
└── overlays/
    ├── jvm/                    # JVM-optimized (default for local dev)
    │   └── kustomization.yaml
    └── native/                 # Native image-optimized (CI/CD)
        ├── kustomization.yaml
        ├── *-image.yaml        # Native image names
        └── *-resources.yaml    # Reduced memory/CPU
```

**Resource Comparison:**

| Configuration | Memory (req/limit) | Startup (liveness/readiness) | Use Case |
|--------------|-------------------|------------------------------|----------|
| **JVM** | 256Mi / 512Mi | 15-45s / 15-30s | Local development, fast iteration |
| **Native** | 64Mi / 128Mi | 10-15s / 5-10s | Production deployment, resource-constrained |

**Deployment Commands:**

```bash
# Deploy using Kustomize directly
kubectl apply -k k8s/overlays/jvm      # JVM images
kubectl apply -k k8s/overlays/native   # Native images

# Delete using Kustomize
kubectl delete -k k8s/overlays/jvm
```

### Kubernetes Operations

```bash
# Check pod status
kubectl get pods -n axis

# View logs
kubectl logs -f <pod-name> -n axis

# Describe resource
kubectl describe pod <pod-name> -n axis

# Restart deployment
kubectl rollout restart deployment/axis-media -n axis

# Port forward to service
kubectl port-forward -n axis service/axis-gateway 8080:8080
```

## Architecture Patterns

### Clean Architecture Layers

All microservices follow strict layered architecture:

```
Entity/Domain -> Repository -> Service (Interface + Impl) -> Controller -> DTOs
```

**Critical conventions:**
- All entities MUST use `UUID` as primary key with `@GeneratedValue(strategy = GenerationType.UUID)`
- Services MUST have interface first, then implementation annotated with `@Service` and `@Transactional`
- Controllers MUST use DTOs for request/response, NEVER expose entities directly
- Use MapStruct for entity-DTO conversions with `@Mapper(componentModel = "spring")`

### Authentication and Security

The platform uses Keycloak for OAuth2/OIDC authentication:

- **Gateway**: Uses WebFlux reactive security with JWT resource server
- **Services**: Standard Spring Security with JWT resource server
- **Custom JWT converter**: `JwtAuthenticationConverter` in axis-common extracts user information from JWT tokens
- **Security utilities**: `SecurityUtils` class provides helper methods to extract user ID, email, and username from JWT

Access current user information:
```java
UUID userId = SecurityUtils.getCurrentUserIdAsUUID().orElseThrow();
String email = SecurityUtils.getCurrentUserEmail().orElse(null);
String username = SecurityUtils.getCurrentUsername().orElse(null);
```

### Exception Handling

Global exception handling is provided in axis-common:
- `GlobalExceptionHandler` with `@RestControllerAdvice` handles all exceptions
- `ResourceNotFoundException`: Returns 404 with standard `ApiError` response
- `BusinessException`: Returns configurable HTTP status with error details
- Validation errors: Automatically converted to structured field-level errors
- Security exceptions: 401 for authentication, 403 for authorization failures

### Database Migrations

Liquibase is used for database migrations:
- Location: `src/main/resources/db/changelog/`
- Format: YAML-based changelogs (e.g., `db.changelog-master.yaml`)
- JPA configuration: `ddl-auto: validate` to prevent schema auto-generation
- Run migrations: Automatic on application startup
- Liquibase provides better support for complex migrations and rollbacks

### Shared Library (axis-common)

Contains:
- **Security**: `JwtAuthenticationConverter`, `SecurityUtils`
- **Exceptions**: `GlobalExceptionHandler`, `ResourceNotFoundException`, `BusinessException`
- **DTOs**: `ApiError` for standardized error responses

All services depend on axis-common and inherit these capabilities.

## Service Configuration Patterns

### Standard application.yaml structure

```yaml
server:
  port: 8083

spring:
  application:
    name: service-name
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # ALWAYS use validate
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
    default-schema: public  # Each service has separate database
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: ${KEYCLOAK_JWK_SET_URI}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

### Environment Variables

Services expect these environment variables (provided via ConfigMaps/Secrets):
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `KEYCLOAK_JWK_SET_URI`: Defaults to `http://keycloak:8080/realms/axis/protocol/openid-connect/certs`

## Kubernetes Deployment

### Deployment Strategy

The project uses **Kustomize overlays** to manage different deployment configurations:
- **JVM overlay** (`k8s/overlays/jvm`): Default for local development with Skaffold
- **Native overlay** (`k8s/overlays/native`): For production CI/CD with reduced resources

All deployments use the same base manifests (`k8s/base/`) with environment-specific patches applied by Kustomize.

### Namespace

All resources deployed to `axis` namespace.

### Services and Ports

- Gateway: http://localhost:8080
- Keycloak: http://localhost:8180
- RabbitMQ Management: http://localhost:15672

### Keycloak Configuration

Realm `axis` is auto-imported with:
- Client: `axis-backend` (secret: `secret`)
- Test users:
  - `testuser` / `testuser`

### Infrastructure Dependencies

- `postgres-keycloak`: Keycloak's database
- `postgres-app`: Application database (for future use)
- `mongodb`: Document storage (media files)
- `rabbitmq`: Message broker
- `redis`: Caching

## Gateway Routing

Gateway routes requests based on path patterns:

```yaml
- Path=/api/goals/**
  -> http://axis-goal:8081

- Path=/api/goal-types/**
  -> http://axis-goal:8081

- Path=/api/notifications/**
  -> http://axis-notification:8082

- Path=/api/media/**
  -> http://axis-media:8083
```

All routes require valid JWT authentication except `/actuator/**`.

## Notification Service (axis-notification)

The notification service manages user notifications and notification preferences.

### Entities

1. **NotificationLog**
   - Tracks all sent notifications
   - Channels: `EMAIL`, `WS` (WebSocket), `TG` (Telegram)
   - Status: `SENT`, `READ`, `FAILED`
   - User-scoped: Each notification belongs to a specific user

2. **NotificationSettings**
   - User notification preferences
   - Per-channel enable/disable flags: `enableEmail`, `enablePush`, `enableTelegram`
   - Default settings: Email and Push enabled, Telegram disabled

3. **NotificationTemplates**
   - Reusable notification templates
   - Types: `SMART_GOAL_DEADLINE`, `NEW_MATCH_IN_BACKLOG`, `PROJECT_STARTED`
   - Contains title and body templates with placeholder support

### API Endpoints

**Notification Logs** (`/api/notifications/logs`):
- `POST /` - Create notification log
- `GET /` - List user's notifications (paginated, sorted by createdAt)
- `GET /{id}` - Get notification by ID
- `GET /status/{status}` - Filter by status (SENT/READ/FAILED)
- `GET /channel/{channel}` - Filter by channel (EMAIL/WS/TG)
- `GET /unread/count` - Count unread notifications
- `PATCH /{id}/status` - Update status (mark as read/failed)
- `DELETE /{id}` - Delete specific notification
- `DELETE /` - Delete all user's notifications

**Notification Settings** (`/api/notifications/settings`):
- `PUT /` - Create or update user preferences
- `GET /` - Get user preferences (returns defaults if none exist)
- `DELETE /` - Delete user preferences (reverts to defaults)

**Notification Templates** (`/api/notifications/templates`):
- `POST /` - Create template (admin only)
- `PUT /{id}` - Update template
- `GET /{id}` - Get template by ID
- `GET /type/{type}` - Get template by type
- `GET /` - List all templates (paginated)
- `DELETE /{id}` - Delete template

### Security

- All endpoints require valid JWT authentication
- Notification logs and settings are user-scoped (users can only access their own data)
- Templates are global (all users can read, but only admins should create/update)
- Uses `SecurityUtils.getCurrentUserIdAsUUID()` for user context

### Database

- Database: `notification` (PostgreSQL)
- User: `notification_user`
- Migration: `V1__init_notifications_schema.sql`
- All tables use UUID primary keys and audit timestamps (`created_at`, `updated_at`)

## Code Quality Standards

- **Naming**: Follow Java conventions (PascalCase for classes, camelCase for methods/fields)
- **Lombok**: Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` to reduce boilerplate
- **Logging**: Use SLF4J with `@Slf4j`, appropriate log levels (DEBUG for SQL, INFO for business events)
- **Null Safety**: Return `Optional<T>` for potentially null values
- **Immutability**: Prefer records for DTOs
- **Dependency Injection**: Constructor injection only (no `@Autowired` fields)
- **Validation**: Use Bean Validation annotations (`@NotNull`, `@NotBlank`, `@Size`) on DTOs
- **OpenAPI**: Document all endpoints with `@Operation`, `@ApiResponse`, `@Tag`

## Common Pitfalls

1. **WebFlux vs WebMVC**: Gateway uses WebFlux (reactive), other services use standard WebMVC. Don't mix dependencies.
2. **Primary Keys**: Always use UUID, never Long/Integer for entity IDs
3. **Service Layer**: Always create interface first, then implementation
4. **Entity Exposure**: Never return entities from controllers, always use DTOs
5. **JPA ddl-auto**: Always use `validate`, never `update` or `create-drop`
6. **Keycloak URL**: Use internal service name `http://keycloak:8080` not `localhost:8180` in configs
7. **Namespace**: Always use `axis` namespace, not `default`
8. **GraalVM Plugin**: DO NOT add `org.graalvm.buildtools.native` plugin to services. Use Spring Boot buildpacks (`bootBuildImage`) exclusively. The GraalVM plugin interferes with Spring AOT processing.
9. **Spring AOT**: Never disable AOT processing (`processAot` task) - it's required for native images. Spring Boot buildpacks handle it automatically.

## Maintaining This Documentation

**IMPORTANT**: Claude must proactively update this CLAUDE.md file after implementing any of the following major changes:

### When to Update CLAUDE.md

- **New Microservices**: Add to the Microservices section with port, purpose, and technology stack
- **New Endpoints/Controllers**: Update Gateway Routing section or add service-specific endpoint documentation
- **Database Schema Changes**: Document new entities, migrations, or schema patterns in relevant sections
- **Authentication/Security Changes**: Update security configuration, new roles, or changed access patterns
- **Infrastructure Changes**: New databases, message queues, caches, or external service integrations
- **Build/Deployment Changes**: Updates to Gradle configuration, Skaffold, Kubernetes manifests, or deployment processes
- **New Architecture Patterns**: Document new conventions, patterns, or coding standards established
- **New Common Pitfalls**: Add issues discovered that developers should be aware of

### Other Documentation Files

While CLAUDE.md is the primary file, keep these files synchronized as the project evolves:

| File | Update When | Purpose |
|------|-------------|---------|
| **CHANGELOG.md** | After completing features, fixes, or releases | Track all notable changes following Keep a Changelog format |
| **.claudeignore** | New build dirs, generated code, large binary directories | Excludes files from Claude's context for better performance |
| **README.md** | Major project changes, setup instructions | General project documentation (Claude reads this too) |
| **.mcp.json** | New MCP servers for databases/tools | External tool integrations (e.g., postgres-app) |

