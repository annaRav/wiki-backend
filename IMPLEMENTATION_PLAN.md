# Wiki Backend - Implementation Plan

**Project**: Wiki Backend Microservices
**Created**: 2025-12-06
**Status**: Planning Phase

---

## Project Overview

A microservices-based wiki platform with organization management, role-based access control, and Keycloak authentication.

### Technology Stack
- **Backend**: Java 21, Spring Boot 3.4.1, Spring Cloud 2024.0.0
- **Security**: Keycloak 24.0, OAuth2, JWT
- **Databases**: PostgreSQL 16, MongoDB 7, Redis 7
- **Messaging**: RabbitMQ 3
- **Infrastructure**: Kubernetes (Minikube), Skaffold
- **Build**: Gradle, Jib

---

## Phase 1: Infrastructure Setup ✅ COMPLETED

**Goal**: Set up the project structure and infrastructure foundation.

### Tasks Completed
- [x] Create multi-module Gradle project structure
- [x] Set up wiki-common shared library
- [x] Create wiki-gateway (Spring Cloud Gateway)
- [x] Create wiki-auth (OAuth2 BFF)
- [x] Create wiki-organization (JPA service)
- [x] Define Kubernetes manifests for all infrastructure
- [x] Configure Skaffold for local development
- [x] Create Flyway database migration (V1)

### Deliverables
- Project structure with 3 microservices + 1 common library
- Kubernetes manifests for PostgreSQL, Keycloak, MongoDB, RabbitMQ, Redis
- Working Skaffold configuration
- Database schema for organizations, roles, permissions

---

## Phase 2: Authentication & Authorization 🔄 NEXT

**Goal**: Implement complete authentication flow and JWT-based authorization.

**Duration**: 1-2 weeks

### 2.1 Keycloak Setup
- [ ] Create `wiki` realm in Keycloak
- [ ] Configure `wiki-backend` client
- [ ] Set up realm roles (admin, user, editor, viewer)
- [ ] Configure client scopes and mappers
- [ ] Test token generation and validation

### 2.2 Auth Service Implementation
- [ ] Implement login endpoint (OAuth2 authorization code flow)
- [ ] Implement logout endpoint
- [ ] Implement token refresh endpoint
- [ ] Get current user profile endpoint
- [ ] User registration endpoint (Keycloak Admin API)
- [ ] Write integration tests

### 2.3 Gateway Security Enhancement
- [ ] Fine-tune route security rules
- [ ] Add request logging filter
- [ ] Implement circuit breaker for downstream services
- [ ] Add health check aggregation

### 2.4 Common Security Enhancements
- [ ] Enhance SecurityUtils with more helper methods
- [ ] Add custom security annotations (@RequireRole, @RequirePermission)
- [ ] Create security audit logging aspect

### Deliverables
- Fully functional authentication flow
- JWT token validation in all services
- User registration capability
- Comprehensive security tests

---

## Phase 3: Organization Management Core 📋 UPCOMING

**Goal**: Implement CRUD operations for organizations and basic role management.

**Duration**: 2-3 weeks

### 3.1 Organization Service - Domain Layer
- [ ] Create Repository interfaces (Organization, Role, Permission)
- [ ] Create Service layer (OrganizationService, RoleService)
- [ ] Implement DTO classes with validation
- [ ] Create MapStruct mappers

### 3.2 Organization REST API
- [ ] `POST /api/organizations` - Create organization
- [ ] `GET /api/organizations` - List user's organizations
- [ ] `GET /api/organizations/{slug}` - Get organization details
- [ ] `PUT /api/organizations/{slug}` - Update organization
- [ ] `DELETE /api/organizations/{slug}` - Delete organization (soft delete)
- [ ] Add pagination and search

### 3.3 Role Management API
- [ ] `GET /api/organizations/{slug}/roles` - List roles
- [ ] `POST /api/organizations/{slug}/roles` - Create role
- [ ] `PUT /api/organizations/{slug}/roles/{roleId}` - Update role
- [ ] `DELETE /api/organizations/{slug}/roles/{roleId}` - Delete role
- [ ] `GET /api/organizations/{slug}/roles/{roleId}/permissions` - Get role permissions
- [ ] `PUT /api/organizations/{slug}/roles/{roleId}/permissions` - Update role permissions

### 3.4 Testing
- [ ] Unit tests for services
- [ ] Integration tests with TestContainers
- [ ] API contract tests

### Deliverables
- Complete organization CRUD API
- Role and permission management
- Comprehensive test coverage
- API documentation (Swagger/OpenAPI)

---

## Phase 4: Member Management 👥 UPCOMING

**Goal**: Implement organization membership and user-role assignments.

**Duration**: 1-2 weeks

### 4.1 Member Management API
- [ ] `GET /api/organizations/{slug}/members` - List members
- [ ] `POST /api/organizations/{slug}/members` - Add member
- [ ] `DELETE /api/organizations/{slug}/members/{userId}` - Remove member
- [ ] `GET /api/organizations/{slug}/members/{userId}/roles` - Get user roles
- [ ] `PUT /api/organizations/{slug}/members/{userId}/roles` - Assign roles
- [ ] Member invitation system

### 4.2 Authorization Layer
- [ ] Implement permission checker service
- [ ] Create @RequirePermission annotation
- [ ] Add method-level security
- [ ] Create permission evaluation logic

### 4.3 User Context Enhancement
- [ ] Get current user's organizations
- [ ] Get current user's permissions in organization
- [ ] Cache user permissions in Redis

### Deliverables
- Member management API
- Fine-grained authorization system
- Permission caching
- Invitation workflow

---

## Phase 5: Wiki Content Foundation 📝 UPCOMING

**Goal**: Create the foundation for wiki content management.

**Duration**: 2-3 weeks

### 5.1 New Service: wiki-content
- [ ] Create wiki-content microservice
- [ ] Use MongoDB for content storage
- [ ] Set up content schema (Page, Section, Revision)
- [ ] Implement version control

### 5.2 Content API
- [ ] `POST /api/organizations/{slug}/pages` - Create page
- [ ] `GET /api/organizations/{slug}/pages` - List pages
- [ ] `GET /api/organizations/{slug}/pages/{pageId}` - Get page
- [ ] `PUT /api/organizations/{slug}/pages/{pageId}` - Update page
- [ ] `DELETE /api/organizations/{slug}/pages/{pageId}` - Delete page
- [ ] `GET /api/organizations/{slug}/pages/{pageId}/history` - Page history

### 5.3 Content Features
- [ ] Markdown support
- [ ] Rich text editor integration
- [ ] Image upload and management
- [ ] Search functionality (Elasticsearch?)

### Deliverables
- Content management service
- Version control for pages
- Basic search functionality
- Image handling

---

## Phase 6: Real-time Collaboration 🔄 UPCOMING

**Goal**: Add real-time features using WebSocket and RabbitMQ.

**Duration**: 2-3 weeks

### 6.1 Real-time Infrastructure
- [ ] Set up WebSocket support in gateway
- [ ] Configure RabbitMQ exchanges and queues
- [ ] Implement event publishing from services

### 6.2 Real-time Features
- [ ] Live page editing indicators
- [ ] Notification system
- [ ] Activity feed
- [ ] User presence tracking

### 6.3 Event-Driven Architecture
- [ ] OrganizationCreatedEvent
- [ ] MemberAddedEvent
- [ ] PageUpdatedEvent
- [ ] RoleChangedEvent

### Deliverables
- WebSocket support
- Event-driven communication
- Real-time notifications
- Activity tracking

---

## Phase 7: Advanced Features 🚀 FUTURE

**Goal**: Add advanced features and optimizations.

**Duration**: 3-4 weeks

### 7.1 Advanced Search
- [ ] Full-text search with Elasticsearch
- [ ] Search across organizations
- [ ] Advanced filtering
- [ ] Search suggestions

### 7.2 Analytics & Monitoring
- [ ] Prometheus metrics
- [ ] Grafana dashboards
- [ ] Distributed tracing (Zipkin/Jaeger)
- [ ] Error tracking (Sentry?)

### 7.3 Performance Optimization
- [ ] Redis caching strategy
- [ ] Database query optimization
- [ ] API response compression
- [ ] CDN for static assets

### 7.4 Additional Features
- [ ] File attachments
- [ ] Comments on pages
- [ ] Page templates
- [ ] Export functionality (PDF, Markdown)

### Deliverables
- Advanced search capability
- Comprehensive monitoring
- Optimized performance
- Rich feature set

---

## Phase 8: Production Readiness 🎯 FUTURE

**Goal**: Prepare the system for production deployment.

**Duration**: 2-3 weeks

### 8.1 Security Hardening
- [ ] Security audit
- [ ] Penetration testing
- [ ] OWASP compliance check
- [ ] Secrets management (Vault?)

### 8.2 DevOps & CI/CD
- [ ] GitHub Actions workflows
- [ ] Automated testing
- [ ] Docker image optimization
- [ ] Production Kubernetes manifests
- [ ] Helm charts

### 8.3 Documentation
- [ ] API documentation (OpenAPI/Swagger)
- [ ] Deployment guide
- [ ] User manual
- [ ] Architecture decision records (ADRs)

### 8.4 Production Infrastructure
- [ ] Production-ready database setup
- [ ] Backup and recovery procedures
- [ ] Load balancer configuration
- [ ] SSL/TLS certificates
- [ ] Domain and DNS setup

### Deliverables
- Production-ready application
- Complete documentation
- CI/CD pipeline
- Monitoring and alerting

---

## Technical Debt & Maintenance

### Ongoing Tasks
- [ ] Keep dependencies up to date
- [ ] Refactor code as needed
- [ ] Performance profiling
- [ ] Security updates
- [ ] Bug fixes

---

## Success Metrics

### Phase 2-3 (MVP)
- Authentication success rate > 99%
- API response time < 200ms (p95)
- Test coverage > 80%

### Phase 4-5 (Beta)
- Support 10+ concurrent users per organization
- 99.9% uptime
- Sub-second search results

### Phase 6-8 (Production)
- Support 100+ organizations
- 1000+ concurrent users
- 99.99% uptime
- Response time < 100ms (p95)

---

## Risk Management

### Technical Risks
- **Risk**: Keycloak configuration complexity
  **Mitigation**: Start with simple setup, document extensively

- **Risk**: Microservices complexity
  **Mitigation**: Keep services focused, use common patterns

- **Risk**: Performance issues with MongoDB
  **Mitigation**: Proper indexing, caching strategy

### Resource Risks
- **Risk**: Development time estimation
  **Mitigation**: Break down into smaller tasks, regular reviews

---

## Notes

- This is a living document - update as project evolves
- Each phase should have its own detailed task breakdown
- Regular retrospectives after each phase
- Adjust timelines based on progress and feedback

---

**Last Updated**: 2025-12-06
**Next Review**: Start of Phase 2
