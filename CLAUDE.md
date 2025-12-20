# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Wiki Backend is a microservices-based wiki platform built with Spring Boot 3.4, Spring Cloud, and Keycloak authentication. The system runs on Kubernetes (Minikube) and uses Skaffold for local development.

### Microservices

- **wiki-gateway** (Port 8080): Spring Cloud Gateway, WebFlux-based reactive API gateway with OAuth2 JWT resource server
- **wiki-membership** (Port 8082): Organization and membership management service with PostgreSQL
- **wiki-common**: Shared library containing security utilities, exception handling, and DTOs

### Technology Stack

- Java 21 (use modern features: records, pattern matching, sealed classes)
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
- Keycloak 24.0 (realm: `wiki`, client: `wiki-backend`)
- PostgreSQL (separate instances for Keycloak and application data)
- MongoDB 7
- RabbitMQ 3
- Redis 7
- Kubernetes (Minikube) with Skaffold

## Build and Development Commands

### Building

```bash
# Build all services
./gradlew clean build

# Build specific service
./gradlew :wiki-membership:build

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

### Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific service
./gradlew :wiki-membership:test

# Run single test class
./gradlew :wiki-membership:test --tests "OrganizationServiceTest"

# Run with coverage
./gradlew test jacocoTestReport
```

### Kubernetes Operations

```bash
# Check pod status
kubectl get pods -n wiki

# View logs
kubectl logs -f <pod-name> -n wiki

# Describe resource
kubectl describe pod <pod-name> -n wiki

# Restart deployment
kubectl rollout restart deployment/wiki-membership -n wiki

# Port forward to service
kubectl port-forward -n wiki service/wiki-gateway 8080:8080
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
- **Custom JWT converter**: `JwtAuthenticationConverter` in wiki-common extracts realm roles and converts to Spring Security authorities
- **Security utilities**: `SecurityUtils` class provides helper methods to extract user ID, email, username, and roles from JWT

Access current user information:
```java
UUID userId = SecurityUtils.getCurrentUserIdAsUUID().orElseThrow();
String email = SecurityUtils.getCurrentUserEmail().orElse(null);
boolean isAdmin = SecurityUtils.hasRole("admin");
```

### Exception Handling

Global exception handling is provided in wiki-common:
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

### Shared Library (wiki-common)

Contains:
- **Security**: `JwtAuthenticationConverter`, `SecurityUtils`
- **Exceptions**: `GlobalExceptionHandler`, `ResourceNotFoundException`, `BusinessException`
- **DTOs**: `ApiError` for standardized error responses

All services depend on wiki-common and inherit these capabilities.

## Service Configuration Patterns

### Standard application.yaml structure

```yaml
server:
  port: 8082

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
- `KEYCLOAK_JWK_SET_URI`: Defaults to `http://keycloak:8080/realms/wiki/protocol/openid-connect/certs`

## Kubernetes Deployment

### Namespace

All resources deployed to `wiki` namespace.

### Services and Ports

- Gateway: http://localhost:8080
- Keycloak: http://localhost:8180
- RabbitMQ Management: http://localhost:15672

### Keycloak Configuration

Realm `wiki` is auto-imported with:
- Client: `wiki-backend` (secret: `secret`)
- Roles: `user`, `admin`
- Test users:
  - `admin` / `admin` (roles: admin, user)
  - `user` / `user` (role: user)

### Infrastructure Dependencies

- `postgres-keycloak`: Keycloak's database
- `postgres-app`: Application database (wiki_membership)
- `mongodb`: Document storage
- `rabbitmq`: Message broker
- `redis`: Caching

## Gateway Routing

Gateway routes requests based on path patterns:

```yaml
- Path=/api/organizations/**,/api/memberships/**,/api/users/**
  -> http://wiki-membership:8082
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