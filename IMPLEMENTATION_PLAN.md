# Axis Backend - Implementation Plan

**Project**: Axis Life Goals Planner
**Created**: 2026-01-02
**Status**: Planning Phase

---

## Project Overview

A microservices-based life goals planning platform with board-based organization (similar to Trello), focused on helping users manage long-term, medium-term, and short-term life goals.

### Technology Stack
- **Backend**: Java 21, Spring Boot 3.4.1, Spring Cloud 2024.0.0
- **Security**: Keycloak 24.0, OAuth2, JWT
- **Databases**: PostgreSQL 16, MongoDB 7, Redis 7
- **Messaging**: RabbitMQ 3
- **Infrastructure**: Kubernetes (Minikube), Skaffold
- **Build**: Gradle, Jib

---

## Migration Status ✅

**Completed**: Migration from wiki-backend to axis-backend (2026-01-02)
- Renamed all modules and packages
- Updated all configurations
- Removed obsolete services (wiki-membership, wiki-message)
- Updated documentation

See [MIGRATION_TO_AXIS.md](MIGRATION_TO_AXIS.md) for complete migration details.

---

## Phase 1: Core Goals Service 📋 NEXT

**Goal**: Implement the foundation for goal management.

### 1.1 Goals Domain Model
- [ ] Design Goal entity (UUID, title, description, type, status, dates)
- [ ] Define GoalType enum (LONG_TERM, MEDIUM_TERM, SHORT_TERM)
- [ ] Define GoalStatus enum (NOT_STARTED, IN_PROGRESS, COMPLETED, ARCHIVED)
- [ ] Create Repository interfaces
- [ ] Set up database migrations (Flyway or Liquibase)

### 1.2 Goals Service Implementation
- [ ] Create axis-goals microservice module
- [ ] Implement GoalService with CRUD operations
- [ ] Add validation and business logic
- [ ] Create DTOs and MapStruct mappers

### 1.3 Goals REST API
- [ ] `POST /api/goals` - Create goal
- [ ] `GET /api/goals` - List user's goals (with filters)
- [ ] `GET /api/goals/{id}` - Get goal details
- [ ] `PUT /api/goals/{id}` - Update goal
- [ ] `DELETE /api/goals/{id}` - Delete/archive goal
- [ ] Add pagination, sorting, and filtering

### 1.4 Testing
- [ ] Unit tests for services
- [ ] Integration tests with TestContainers
- [ ] API contract tests

### Deliverables
- Goals microservice with complete CRUD
- Database schema for goals
- REST API with documentation
- Test coverage > 80%

---

## Phase 2: Board & Workspace Management 🗂️ UPCOMING

**Goal**: Implement Trello-like board system for organizing goals.

### 2.1 Board Domain Model
- [ ] Design Board entity
- [ ] Design Column/List entity
- [ ] Design Card entity (links to Goals)
- [ ] Establish relationships

### 2.2 Board Service
- [ ] Board CRUD operations
- [ ] Column management
- [ ] Card positioning and ordering
- [ ] Drag-and-drop logic

### 2.3 Board API
- [ ] Board management endpoints
- [ ] Column management endpoints
- [ ] Card management endpoints
- [ ] Reordering operations

### Deliverables
- Board microservice
- Complete board management API
- Drag-and-drop support

---

## Phase 3: Progress Tracking & Analytics 📊 UPCOMING

**Goal**: Add progress tracking and visualization.

### 3.1 Progress System
- [ ] Milestone tracking
- [ ] Sub-goals/tasks
- [ ] Progress calculation
- [ ] Completion metrics

### 3.2 Analytics Service
- [ ] Goals statistics
- [ ] Progress over time
- [ ] Achievement tracking
- [ ] Reports generation

### Deliverables
- Progress tracking system
- Analytics dashboard data
- Reports API

---

## Phase 4: Collaboration Features 👥 UPCOMING

**Goal**: Enable sharing and collaboration on goals.

### 4.1 Sharing & Permissions
- [ ] Goal sharing mechanism
- [ ] Permission levels (view, edit, admin)
- [ ] Collaborative boards

### 4.2 Activity Feed
- [ ] Activity tracking
- [ ] Notifications system
- [ ] Real-time updates (WebSocket)

### Deliverables
- Sharing and permissions system
- Activity feed
- Real-time notifications

---

## Phase 5: Advanced Features 🚀 FUTURE

### 5.1 Templates
- [ ] Goal templates
- [ ] Board templates
- [ ] Pre-built workflows

### 5.2 Reminders & Scheduling
- [ ] Deadline reminders
- [ ] Recurring goals
- [ ] Calendar integration

### 5.3 Attachments & Media
- [ ] File attachments using axis-media
- [ ] Image uploads
- [ ] Document linking

### Deliverables
- Template system
- Reminder service
- Enhanced media support

---

## Phase 6: Mobile & Frontend 📱 FUTURE

### 6.1 Mobile API Optimization
- [ ] GraphQL API (optional)
- [ ] API versioning
- [ ] Performance optimization

### 6.2 Frontend Development
- [ ] Web application (React/Vue/Angular)
- [ ] Mobile apps (React Native/Flutter)
- [ ] Progressive Web App (PWA)

### Deliverables
- Optimized APIs
- Web application
- Mobile applications

---

## Phase 7: Production Readiness 🎯 FUTURE

### 7.1 Performance & Scalability
- [ ] Caching strategy (Redis)
- [ ] Database optimization
- [ ] Load testing
- [ ] Horizontal scaling

### 7.2 Security Hardening
- [ ] Security audit
- [ ] Penetration testing
- [ ] OWASP compliance

### 7.3 Monitoring & Observability
- [ ] Prometheus metrics
- [ ] Grafana dashboards
- [ ] Distributed tracing
- [ ] Error tracking

### 7.4 CI/CD & Deployment
- [ ] GitHub Actions workflows
- [ ] Automated testing
- [ ] Production Kubernetes setup
- [ ] Helm charts

### Deliverables
- Production-ready infrastructure
- Complete monitoring setup
- Automated CI/CD pipeline

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

### Phase 1-2 (MVP)
- API response time < 200ms (p95)
- Test coverage > 80%
- Support CRUD for goals and boards

### Phase 3-4 (Beta)
- Support 100+ concurrent users
- Real-time updates < 1s latency
- 99.5% uptime

### Phase 5-7 (Production)
- Support 1000+ concurrent users
- API response time < 100ms (p95)
- 99.9% uptime
- Mobile apps in stores

---

## Notes

- This is a living document - update as project evolves
- Each phase should have detailed task breakdown
- Regular retrospectives after each phase
- Prioritize based on user feedback

---

**Last Updated**: 2026-01-02
**Next Review**: Start of Phase 1 implementation
