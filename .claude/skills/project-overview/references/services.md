# Service Details

## Currently Implemented Services

### wiki-gateway (Port 8080)

API Gateway - single entry point for all clients.

**Responsibilities:**
- Route requests to appropriate services
- JWT authentication/authorization with Keycloak
- CORS handling
- Request/response logging

**Technology:**
- Spring Cloud Gateway (WebFlux/Reactive)
- Spring Security OAuth2 Resource Server
- Keycloak JWT validation

**Configuration:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: membership-service
          uri: http://wiki-membership:8082
          predicates:
            - Path=/api/organizations/**,/api/memberships/**,/api/users/**
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://keycloak:8080/realms/wiki/protocol/openid-connect/certs
```

**Security:**
- All routes require valid JWT (except `/actuator/**`)
- Custom `JwtAuthenticationConverter` extracts Keycloak realm roles
- Converts roles to Spring Security authorities (ROLE_user, ROLE_admin)

---

### wiki-membership (Port 8082)

Organization and membership management service.

**Responsibilities:**
- Organization CRUD operations
- User management within organizations
- Role and permission management
- Membership tracking

**Technology:**
- Spring Boot Web (MVC)
- Spring Data JPA + PostgreSQL
- Flyway migrations
- Spring Security OAuth2 Resource Server

**Database:** PostgreSQL (wiki_membership)
- Table: `organizations`
- Table: `users`
- Table: `roles`
- Table: `permissions`
- Table: `user_roles`
- Table: `role_permissions`

**Key Endpoints:**
```
GET    /api/organizations
POST   /api/organizations
GET    /api/organizations/{id}
PUT    /api/organizations/{id}
DELETE /api/organizations/{id}
GET    /api/organizations/{id}/members
```

**Architecture:**
- Clean layered architecture: Entity → Repository → Service (Interface + Impl) → Controller → DTOs
- UUID primary keys for all entities
- MapStruct for entity-DTO conversions
- Bean Validation on DTOs
- Global exception handling via wiki-common

---

### wiki-common (Shared Library)

Common components used across all microservices.

**Components:**
- **Security:**
  - `JwtAuthenticationConverter`: Converts Keycloak JWT to Spring Security authentication
  - `SecurityUtils`: Helper methods to extract user ID, email, roles from JWT

- **Exception Handling:**
  - `GlobalExceptionHandler`: Centralized exception handling with `@RestControllerAdvice`
  - `ResourceNotFoundException`: 404 exception
  - `BusinessException`: Configurable HTTP status exception
  - `ApiError`: Standardized error response DTO

- **DTOs:**
  - `ApiError`: Error response with timestamp, status, message, path, field errors

**Usage:**
All microservices depend on wiki-common and inherit these capabilities automatically.

---

## Planned Future Services

### wiki-content (Port 8081)

Wiki pages and content management.

**Planned Responsibilities:**
- CRUD for wiki pages
- Version control (revisions)
- Markdown processing
- Content categories/tags

**Planned Database:** MongoDB
- Collection: `pages`
- Collection: `revisions`
- Collection: `categories`

---

### wiki-search (Port 8083)

Full-text search service.

**Planned Responsibilities:**
- Indexing wiki pages
- Full-text search
- Search suggestions
- Faceted search

**Planned Database:** Elasticsearch

---

### wiki-media (Port 8084)

Media file management.

**Planned Responsibilities:**
- File upload/download
- Image processing (resize, thumbnails)
- Metadata extraction
- Storage management (S3-compatible)

**Planned Database:** MongoDB (metadata) + S3 (files)