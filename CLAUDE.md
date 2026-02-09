# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Axis Backend is a microservices-based life goals planning platform (similar to Trello) built with Quarkus 3.x and Keycloak authentication. The system helps users organize and track long-term, medium-term, and short-term life goals using a board-based interface. The platform runs on Kubernetes (Minikube) and uses Skaffold for local development.

### Microservices

- **axis-goal** (Port 8081): Goals management service with PostgreSQL storage for long/medium/short-term goals, goal types, and custom fields
- **axis-notification** (Port 8082): Notification management service with PostgreSQL storage for notification logs, user settings, and templates
- **axis-media** (Port 8083): Media file management service with MongoDB storage
- **axis-common**: Shared library containing security utilities, exception handling, and DTOs
- **Gateway**: Nginx Ingress Controller handles routing (no separate gateway service)

### Technology Stack

- Java 21 (use modern features: records, pattern matching, sealed classes)
- Quarkus 3.x (supersonic subatomic Java framework)
- GraalVM Native Image (for CI/CD production builds)
- Keycloak 24.0 (realm: `axis`, client: `axis-backend`)
- PostgreSQL (separate instances for Keycloak and application data)
- MongoDB 7
- RabbitMQ 3
- Redis 7
- Kubernetes (Minikube) with Skaffold
- Nginx Ingress Controller for API routing

### Key Quarkus Extensions Used

All services use these core extensions:
- **quarkus-rest** (JAX-RS implementation, replaces Spring MVC)
- **quarkus-rest-jackson** (JSON serialization)
- **quarkus-hibernate-orm-panache** (Simplified JPA with Panache patterns)
- **quarkus-jdbc-postgresql** (PostgreSQL driver)
- **quarkus-liquibase** (Database migrations)
- **quarkus-oidc** (Keycloak integration)
- **quarkus-smallrye-jwt** (JWT processing)
- **quarkus-smallrye-health** (Health checks at `/actuator/health`)
- **quarkus-micrometer-registry-prometheus** (Metrics at `/actuator/prometheus`)
- **quarkus-smallrye-openapi** (OpenAPI/Swagger documentation)
- **quarkus-hibernate-validator** (Bean Validation)

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

### Two-Mode Development Strategy

The project uses exactly **2 modes** via a single `skaffold.yaml`:

- **Dev** (`skaffold dev`): Builds JVM images locally in Minikube, deploys everything (infra + services), Skaffold rebuilds on file changes
- **Prod** (`skaffold run -p prod`): Pulls native images from GitHub Container Registry (ghcr.io), no local builds

### Local Development (Recommended)

```bash
# Start Minikube (if not running)
minikube start

# Point Docker CLI to Minikube's Docker daemon
eval $(minikube docker-env)

# Build JVM images, deploy all infra + services, port-forward everything
skaffold dev
```

Skaffold will build 3 JVM Docker images, deploy all infrastructure (PostgreSQL, Keycloak, MongoDB, RabbitMQ, Redis) and all services, and set up port-forwarding. On code changes, Skaffold automatically rebuilds and redeploys the affected service.

**Access Points:**
- API Gateway: http://localhost:8080
- Keycloak: http://localhost:8180
- RabbitMQ Management: http://localhost:15672
- PostgreSQL: localhost:5433
- MongoDB: localhost:27017
- Redis: localhost:6379

### Testing Production Images

Pull and test production-ready native images from GitHub Container Registry:

```bash
# Deploy native images from GHCR (no local builds)
skaffold run -p prod

# Delete deployment
skaffold delete -p prod
```

This pulls pre-built images from `ghcr.io/annarav/axis-*:latest` - no local compilation needed!

### Building Native Images (CI/CD Only)

**IMPORTANT:** Native images are built automatically in GitHub Actions. Do NOT build them locally unless debugging native compilation issues.

```bash
# Build native executable (takes 5-10 minutes per service!)
./gradlew :axis-goal:build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

### Kustomize Structure

The project uses a **simplified Kustomize structure** with 2 overlays:

```
k8s/
├── base/                       # Base manifests (all services and infrastructure)
│   ├── kustomization.yaml
│   ├── namespace.yaml
│   ├── gateway-api.yaml        # Nginx Ingress routing
│   ├── axis-goal.yaml
│   ├── axis-media.yaml
│   ├── axis-notification.yaml
│   ├── infrastructure/         # Keycloak, PostgreSQL, MongoDB, RabbitMQ, Redis
│   └── config/                 # ConfigMaps and Secrets
└── overlays/
    ├── dev/                    # For local development (adds env label only)
    │   └── kustomization.yaml
    └── prod/                   # For production (GHCR images + native resources)
        ├── kustomization.yaml
        └── prod-patches.yaml   # Native resources (low memory/fast startup)
```

**Resource Comparison:**

| Configuration | Memory (req/limit) | Startup (liveness/readiness) | Use Case |
|--------------|-------------------|------------------------------|----------|
| **Dev (JVM)** | 256Mi / 512Mi | 45s / 30s | Local development with JVM images |
| **Prod (Native)** | 64Mi / 128Mi | 15s / 10s | Production from GitHub Container Registry |

**Deployment Commands:**

```bash
# Dev: build JVM images locally, deploy everything
skaffold dev

# Prod: pull native images from GHCR, deploy everything
skaffold run -p prod

# Delete deployment
skaffold delete          # dev
skaffold delete -p prod  # prod
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
Entity/Domain -> Repository -> Service (Interface + Impl) -> Resource (REST endpoint) -> DTOs
```

**Critical conventions:**
- All entities MUST use `UUID` as primary key with `@GeneratedValue(strategy = GenerationType.UUID)`
- Services MUST have interface first, then implementation annotated with `@ApplicationScoped` and `@Transactional`
- REST Resources use JAX-RS annotations (`@Path`, `@GET`, `@POST`, etc.) not Spring MVC
- Resources MUST use DTOs for request/response, NEVER expose entities directly
- Use MapStruct for entity-DTO conversions with `@Mapper(componentModel = "jakarta")`
- Use Panache repositories for simplified data access (optional but recommended)

### Authentication and Security

The platform uses Keycloak for OAuth2/OIDC authentication:

- **Routing**: Nginx Ingress Controller handles routing to services
- **Services**: Quarkus OIDC extension (`quarkus-oidc`) with JWT resource server
- **Security utilities**: `SecurityUtils` class in axis-common provides helper methods to extract user information from JWT
- **Exception handling**: `ExceptionMappers` class provides JAX-RS exception mappers for consistent error responses

Access current user information:
```java
UUID userId = SecurityUtils.getCurrentUserIdAsUUID().orElseThrow();
String email = SecurityUtils.getCurrentUserEmail().orElse(null);
String username = SecurityUtils.getCurrentUsername().orElse(null);
```

**Securing endpoints:**
```java
@Path("/api/goals")
@Authenticated  // Require authentication for all endpoints in this resource
public class GoalResource {

    @GET
    @RolesAllowed("user")  // Specific role requirement
    public List<GoalResponse> list() { ... }

    @POST
    @PermitAll  // Allow all authenticated users
    public GoalResponse create(GoalRequest request) { ... }
}
```

### Exception Handling

Global exception handling uses JAX-RS ExceptionMappers:
- `ExceptionMappers` class with `@Provider` annotated methods handles all exceptions
- `ResourceNotFoundException`: Returns 404 with standard error response
- `BusinessException`: Returns configurable HTTP status with error details
- Validation errors: Bean Validation exceptions automatically converted to structured field-level errors
- Security exceptions: 401 for authentication, 403 for authorization failures

**Example ExceptionMapper:**
```java
@Provider
public class ExceptionMappers {

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapResourceNotFound(ResourceNotFoundException ex) {
        return RestResponse.status(Response.Status.NOT_FOUND,
            new ErrorResponse(ex.getMessage()));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapBusinessException(BusinessException ex) {
        return RestResponse.status(ex.getStatus(),
            new ErrorResponse(ex.getMessage()));
    }
}
```

### Database Migrations

Liquibase is used for database migrations via `quarkus-liquibase` extension:
- Location: `src/main/resources/db/changelog/`
- Format: YAML-based changelogs (e.g., `db.changelog-master.yaml`)
- Configuration: `quarkus.liquibase.migrate-at-start=true` for automatic migrations
- Run migrations: Automatic on application startup
- Schema validation: Hibernate should be set to `validate` only
- Liquibase provides better support for complex migrations and rollbacks

### Shared Library (axis-common)

Contains:
- **Security**: `SecurityUtils` for extracting user information from JWT
- **Exceptions**: `ExceptionMappers` (JAX-RS providers), `ResourceNotFoundException`, `BusinessException`
- **DTOs**: Error response DTOs for standardized error responses

All services depend on axis-common and inherit these capabilities. The library is compatible with Quarkus CDI and uses Jakarta EE annotations.

## Service Configuration Patterns

### Standard application.properties structure

Quarkus uses `application.properties` by default (not YAML):

```properties
# HTTP Configuration
quarkus.http.port=8081

# Application Name
quarkus.application.name=axis-goal

# Database Configuration
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:goal}
quarkus.datasource.username=${DB_USERNAME:goal_user}
quarkus.datasource.password=${DB_PASSWORD:goal_password}

# Hibernate ORM
quarkus.hibernate-orm.database.generation=validate
quarkus.hibernate-orm.log.sql=false

# Liquibase
quarkus.liquibase.migrate-at-start=true
quarkus.liquibase.change-log=db/changelog/db.changelog-master.yaml

# OIDC (Keycloak)
quarkus.oidc.auth-server-url=${KEYCLOAK_ISSUER_URI:http://keycloak:8080/realms/axis}
quarkus.oidc.client-id=axis-backend
quarkus.oidc.credentials.secret=${KEYCLOAK_CLIENT_SECRET:secret}

# SmallRye Health
quarkus.smallrye-health.root-path=/actuator/health

# Micrometer Prometheus
quarkus.micrometer.export.prometheus.path=/actuator/prometheus

# OpenAPI / Swagger
quarkus.smallrye-openapi.path=/swagger
quarkus.swagger-ui.always-include=true

# Dev Mode (only active in dev profile)
%dev.quarkus.log.console.level=DEBUG
```

### Environment Variables

Services expect these environment variables (provided via ConfigMaps/Secrets):
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `KEYCLOAK_ISSUER_URI`: Defaults to `http://keycloak:8080/realms/axis`
- `KEYCLOAK_CLIENT_SECRET`: Client secret for OIDC (defaults to `secret`)

## Kubernetes Deployment

### Deployment Strategy

The project uses **simplified Kustomize overlays** with 2 configurations:
- **Dev overlay** (`k8s/overlays/dev`): Uses base manifests with JVM resource settings (no patches needed)
- **Prod overlay** (`k8s/overlays/prod`): Native images from GitHub Container Registry with reduced resource limits

All deployments use the same base manifests (`k8s/base/`). Dev overlay adds only an environment label. Prod overlay applies resource patches and swaps images to ghcr.io.

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

## API Routing

Nginx Ingress Controller routes requests based on path patterns:

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

Authentication is handled by each service using Quarkus OIDC extension. Health and metrics endpoints (`/actuator/**`) are typically public or secured at the infrastructure level.

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
- **Lombok**: Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` to reduce boilerplate (works with Quarkus)
- **Logging**: Use JBoss Logging with `@Slf4j` or Quarkus `Log` class, appropriate log levels
- **Null Safety**: Return `Optional<T>` for potentially null values
- **Immutability**: Prefer records for DTOs
- **Dependency Injection**: Use `@Inject` for constructor injection (not `@Autowired`)
- **Scoping**: Use `@ApplicationScoped` for services, `@RequestScoped` for request-specific beans
- **Validation**: Use Bean Validation annotations (`@NotNull`, `@NotBlank`, `@Size`) on DTOs
- **OpenAPI**: Use MicroProfile OpenAPI annotations (`@Operation`, `@APIResponse`, `@Tag`)
- **REST Resources**: Use JAX-RS annotations (`@Path`, `@GET`, `@POST`, `@Produces`, `@Consumes`)
- **Transactions**: Use `@Transactional` on service methods that modify data

## Common Pitfalls

1. **JAX-RS not Spring MVC**: Use `@Path`, `@GET`, `@POST` not `@RequestMapping`, `@GetMapping`
2. **Dependency Injection**: Use `@Inject` not `@Autowired`, and `@ApplicationScoped` not `@Service`
3. **Primary Keys**: Always use UUID, never Long/Integer for entity IDs
4. **Service Layer**: Always create interface first, then implementation with `@ApplicationScoped`
5. **Entity Exposure**: Never return entities from REST resources, always use DTOs
6. **Hibernate Generation**: Always use `validate`, never `update` or `create-drop` in production
7. **Keycloak URL**: Use internal service name `http://keycloak:8080` not `localhost:8180` in configs
8. **Namespace**: Always use `axis` namespace, not `default`
9. **Native Builds**: DO NOT build native images locally - they take 5-10 minutes. Let CI/CD handle it.
10. **Dev Mode**: Use `skaffold dev` for full-stack development (builds JVM images + deploys everything)
11. **Configuration**: Use `application.properties` not `application.yaml` (Quarkus convention)
12. **MapStruct**: Use `componentModel = "jakarta"` not `"spring"` for Quarkus CDI compatibility
13. **Transactions**: Use `@Transactional` from `jakarta.transaction`, not Spring's version
14. **REST Client**: Use Quarkus REST Client (`@RegisterRestClient`) not Spring's RestTemplate or WebClient

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
| **QUARKUS-DEV-WORKFLOW.md** | Workflow changes, new profiles, troubleshooting | Detailed Quarkus development guide with examples and CI/CD setup |
| **CHANGELOG.md** | After completing features, fixes, or releases | Track all notable changes following Keep a Changelog format |
| **.claudeignore** | New build dirs, generated code, large binary directories | Excludes files from Claude's context for better performance |
| **README.md** | Major project changes, setup instructions | General project documentation (Claude reads this too) |
| **.mcp.json** | New MCP servers for databases/tools | External tool integrations (e.g., postgres-app) |

## Additional Resources

- 📚 **[QUARKUS-DEV-WORKFLOW.md](./QUARKUS-DEV-WORKFLOW.md)** - Comprehensive guide to Quarkus development workflow
- 🔗 [Quarkus Guides](https://quarkus.io/guides/) - Official Quarkus documentation
- 🔗 [Quarkus Extensions](https://quarkus.io/extensions/) - Available Quarkus extensions
- 🔗 [Panache Guide](https://quarkus.io/guides/hibernate-orm-panache) - Simplified JPA with Panache

