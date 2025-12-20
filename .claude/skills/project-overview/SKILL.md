
---
name: project-overview
description: Wiki Platform microservices architecture overview. Use when asking about project structure, service responsibilities, inter-service communication, database usage, or when needing context about the overall system before implementing features.
---

# Wiki Platform Architecture

## System Overview

Wiki Platform is a microservices-based system for creating and managing wiki content with enterprise-grade authentication and authorization.

**Tech Stack:**
- Backend: Spring Boot 3.4.1, Spring Cloud 2024.0.0, Java 21
- Authentication: Keycloak 24.0 (OAuth2/OIDC)
- Databases: PostgreSQL (structured data), MongoDB (content/documents)
- Message Queue: RabbitMQ 3
- Cache: Redis 7
- Infrastructure: Kubernetes (Minikube for dev), Skaffold
- Communication: REST APIs, async messaging (RabbitMQ)

## Microservices

**Currently Implemented:**

| Service         | Port | Database      | Responsibility                           |
|-----------------|------|---------------|------------------------------------------|
| wiki-gateway    | 8080 | -             | API Gateway (Spring Cloud Gateway), JWT auth, routing |
| wiki-membership | 8082 | PostgreSQL    | Organizations, users, roles, permissions |
| wiki-common     | -    | -             | Shared library: security, exceptions, DTOs |

**Planned for Future:**

| Service         | Port | Database      | Responsibility                           |
|-----------------|------|---------------|------------------------------------------|
| wiki-content    | 8081 | MongoDB       | Pages, articles, revisions               |
| wiki-search     | 8083 | Elasticsearch | Full-text search, indexing               |
| wiki-media      | 8084 | MongoDB + S3  | Images, attachments                      |

## Service Communication

```
[Client] → [Gateway:8080] → [Membership/Content/Search/Media]
                ↓
    [Keycloak JWT Validation]
         [Role-based routing]
```

**Patterns:**
- Sync: REST calls between services
- Auth: JWT tokens issued by Keycloak, validated at gateway and each service
- Discovery: Kubernetes DNS (e.g., `http://wiki-membership:8082`)
- Gateway routing based on path patterns

**Authentication & Authorization:**
- Keycloak realm: `wiki`
- Client: `wiki-backend`
- Roles: `user`, `admin`
- Test users: `admin/admin`, `user/user`
- Custom `JwtAuthenticationConverter` extracts Keycloak realm roles
- `SecurityUtils` helper provides access to user ID, email, roles from JWT

## Database Strategy

**PostgreSQL** (structured, relational data):
- Organizations, users, roles, permissions (wiki-membership)
- Audit logs
- Configuration
- **Note:** All entities use UUID primary keys
- **Migrations:** Flyway in `src/main/resources/db/migration/`
- **JPA mode:** `ddl-auto: validate` (never auto-generate schema)

**MongoDB** (flexible, document data - planned):
- Wiki pages and revisions
- Comments
- Media metadata

## Project Structure

```
wiki-backend/
├── wiki-gateway/          # API Gateway (WebFlux)
├── wiki-membership/       # Membership service (Web MVC)
├── wiki-common/           # Shared library
├── k8s/                   # Kubernetes manifests
│   ├── namespace.yaml
│   ├── config/           # ConfigMaps, Secrets
│   ├── infrastructure/   # Postgres, Keycloak, MongoDB, RabbitMQ, Redis
│   └── services/         # Service deployments
├── skaffold.yaml         # Skaffold configuration
└── build.gradle          # Root Gradle build
```

## Key Architecture Conventions

**Clean Architecture Layers:**
```
Entity → Repository → Service (Interface + Impl) → Controller → DTOs
```

**Mandatory patterns:**
- UUID primary keys for all entities (`@GeneratedValue(strategy = GenerationType.UUID)`)
- Service layer: Interface first, then implementation
- DTOs for API contracts, never expose entities
- MapStruct for entity-DTO conversions
- Constructor injection (no field `@Autowired`)
- OpenAPI documentation on all endpoints
- Bean Validation on DTOs

**Shared components (wiki-common):**
- `GlobalExceptionHandler`: Centralized exception handling
- `SecurityUtils`: Extract user info from JWT
- `JwtAuthenticationConverter`: Convert Keycloak roles to Spring authorities
- `ApiError`: Standardized error response DTO

## Development Environment

**Local deployment:**
```bash
minikube start
eval $(minikube docker-env)
skaffold dev  # Auto-reload on code changes
```

**Access points:**
- Gateway: http://localhost:8080
- Keycloak Admin: http://localhost:8180
- RabbitMQ Management: http://localhost:15672

**Build:**
```bash
./gradlew clean build                 # All services
./gradlew :wiki-membership:build      # Single service
```

For detailed information, see `CLAUDE.md` in the repository root.
