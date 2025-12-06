# Wiki Gateway Skills

## Overview
Skills and capabilities for the wiki-gateway service.

## Core Responsibilities
- API Gateway routing
- JWT token validation
- CORS handling
- Request/response transformation
- Rate limiting (future)

## Technical Skills Required
- Spring Cloud Gateway
- Reactive programming (WebFlux)
- OAuth2 Resource Server
- Circuit breaker patterns

## API Routes
- `/api/auth/**` → wiki-auth:8081
- `/api/organizations/**` → wiki-organization:8082

## Security
- All routes require JWT authentication except `/api/auth/**`
- Extracts roles from Keycloak JWT `realm_access.roles`

## Future Enhancements
- [ ] Rate limiting per user/organization
- [ ] API versioning support
- [ ] Request/response logging
- [ ] Metrics and tracing
