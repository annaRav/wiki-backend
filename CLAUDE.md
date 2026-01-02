# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Axis Backend is a microservices-based life goals planning platform (similar to Trello) built with Spring Boot 3.4, Spring Cloud, and Keycloak authentication. The system helps users organize and track long-term, medium-term, and short-term life goals using a board-based interface. The platform runs on Kubernetes (Minikube) and uses Skaffold for local development.

### Microservices

- **axis-gateway** (Port 8080): Spring Cloud Gateway, WebFlux-based reactive API gateway with OAuth2 JWT resource server
- **axis-media** (Port 8083): Media file management service with MongoDB storage
- **axis-common**: Shared library containing security utilities, exception handling, and DTOs

### Technology Stack

- Java 21 (use modern features: records, pattern matching, sealed classes)
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
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

### Building

```bash
# Build all services
./gradlew clean build

# Build specific service
./gradlew :axis-media:build

# Build Docker images (uses Jib)
./gradlew jibDockerBuild
```

### Local Development with Skaffold

```bash
# Start Minikube (if not running)
minikube start

# Point Docker CLI to Minikube's Docker daemon
eval $(minikube docker-env)

# Deploy and run in development mode (auto-reload on changes)
skaffold dev

# Deploy without watching
skaffold run

# Delete deployment
skaffold delete
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
- **Custom JWT converter**: `JwtAuthenticationConverter` in axis-common extracts realm roles and converts to Spring Security authorities
- **Security utilities**: `SecurityUtils` class provides helper methods to extract user ID, email, username, and roles from JWT

Access current user information:
```java
UUID userId = SecurityUtils.getCurrentUserIdAsUUID().orElseThrow();
String email = SecurityUtils.getCurrentUserEmail().orElse(null);
boolean isAdmin = SecurityUtils.hasRole("admin");
```

### Exception Handling

Global exception handling is provided in axis-common:
- `GlobalExceptionHandler` with `@RestControllerAdvice` handles all exceptions
- `ResourceNotFoundException`: Returns 404 with standard `ApiError` response
- `BusinessException`: Returns configurable HTTP status with error details
- Validation errors: Automatically converted to structured field-level errors
- Security exceptions: 401 for authentication, 403 for authorization failures

### Database Migrations

Flyway is used for database migrations:
- Location: `src/main/resources/db/migration/`
- Naming: `V{version}__{description}.sql` (e.g., `V1__init_schema.sql`)
- JPA configuration: `ddl-auto: validate` to prevent schema auto-generation
- Run migrations: Automatic on application startup

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
  flyway:
    enabled: true
    baseline-on-migrate: true
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

### Namespace

All resources deployed to `axis` namespace.

### Services and Ports

- Gateway: http://localhost:8080
- Keycloak: http://localhost:8180
- RabbitMQ Management: http://localhost:15672

### Keycloak Configuration

Realm `axis` is auto-imported with:
- Client: `axis-backend` (secret: `secret`)
- Roles: `user`, `admin`
- Test users:
  - `admin` / `admin` (roles: admin, user)
  - `user` / `user` (role: user)

### Infrastructure Dependencies

- `postgres-keycloak`: Keycloak's database
- `postgres-app`: Application database (for future use)
- `mongodb`: Document storage (media files)
- `rabbitmq`: Message broker
- `redis`: Caching

## Gateway Routing

Gateway routes requests based on path patterns:

```yaml
- Path=/api/media/**
  -> http://axis-media:8083
```

All routes require valid JWT authentication except `/actuator/**`.

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
7. **Namespace**: Always use `axis` namespace, not `wiki` or `default`

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

## Migration History

This project was migrated from wiki-backend to axis-backend on 2026-01-02. The platform's focus shifted from a wiki system to a life goals planning platform. See [MIGRATION_TO_AXIS.md](MIGRATION_TO_AXIS.md) for complete migration details.
