# Migration Plan: Wiki Backend → Axis Backend

**Transformation**: Wiki Platform → Axis Life Goals Planner (Trello-like)
**Created**: 2026-01-02
**Status**: Planning Phase

---

## Executive Summary

This document outlines the complete migration plan to transform the wiki-backend project into axis-backend, a life goals planning system similar to Trello, but focused on long-term, medium-term, and short-term goals.

### Key Changes
- **Project name**: `wiki-backend` → `axis-backend`
- **Modules to delete**: `wiki-membership`, `wiki-message`
- **Modules to rename**:
  - `wiki-gateway` → `axis-gateway`
  - `wiki-media` → `axis-media`
  - `wiki-common` → `axis-common`
- **Java packages**: `com.wiki.*` → `com.axis.*`
- **Kubernetes namespace**: `wiki` → `axis`
- **Keycloak realm**: `wiki` → `axis`
- **Docker images**: `wiki/*` → `axis/*`

---

## Phase 1: Pre-Migration Preparation ⚠️

**Goal**: Ensure safe migration with backup and understanding of dependencies.

### 1.1 Backup Current State
```bash
# Create git branch for migration
git checkout -b feature/migrate-to-axis

# Create backup of current state
git tag backup-before-axis-migration

# Verify all changes are committed
git status
```

### 1.2 Document Current Dependencies
- [ ] List all database schemas in use (postgres-app databases)
- [ ] Document Keycloak configurations (realm, clients, users)
- [ ] List all external integrations
- [ ] Verify current deployment works with `skaffold dev`

### 1.3 Analyze Services to Remove
**wiki-membership**:
- Check dependencies in other services
- Identify shared code that might be needed
- Document database tables used

**wiki-message**:
- Check dependencies in other services
- Identify RabbitMQ queues/exchanges
- Document database collections used

### 1.4 Plan New Domain Model
- [ ] Design Goal entity (long-term, medium-term, short-term)
- [ ] Design Board/Project entity
- [ ] Design Task/Action entity
- [ ] Design User workspace structure

---

## Phase 2: Delete Obsolete Services 🗑️

**Goal**: Clean removal of wiki-membership and wiki-message services.

### 2.1 Remove wiki-membership Service

#### Gradle Configuration
```bash
# Files to modify:
- settings.gradle (remove include 'wiki-membership')
- build.gradle (remove any dependencies)
```

#### Skaffold Configuration
```bash
# Files to modify:
- skaffold.yaml (remove wiki/membership artifact and manifest)
```

#### Kubernetes Manifests
```bash
# Files to delete:
- k8s/services/wiki-membership.yaml
```

#### Source Code
```bash
# Directories to delete:
rm -rf wiki-membership/
```

#### Database
- [ ] Drop `wiki_membership` database or document migration
- [ ] Update postgres-app configuration if needed

### 2.2 Remove wiki-message Service

#### Gradle Configuration
```bash
# Files to modify:
- settings.gradle (remove include 'wiki-message')
```

#### Skaffold Configuration
```bash
# Files to modify:
- skaffold.yaml (remove wiki/message artifact, manifest, and port-forward)
```

#### Kubernetes Manifests
```bash
# Files to delete:
- k8s/services/wiki-message.yaml
```

#### Source Code
```bash
# Directories to delete:
rm -rf wiki-message/
```

### 2.3 Verify Removal
- [ ] Check for broken imports in remaining services
- [ ] Search for references: `git grep -i "wiki-membership\|wiki-message"`
- [ ] Verify Gradle builds: `./gradlew clean build`

---

## Phase 3: Rename Gradle Modules 🔄

**Goal**: Rename all remaining modules from wiki-* to axis-*.

### 3.1 Update settings.gradle
```gradle
rootProject.name = 'axis-backend'

include 'axis-common'
include 'axis-gateway'
include 'axis-media'
```

### 3.2 Rename Module Directories
```bash
# Execute these commands:
mv wiki-common axis-common
mv wiki-gateway axis-gateway
mv wiki-media axis-media
```

### 3.3 Update build.gradle Files

#### Root build.gradle
- Update project descriptions
- Update group: `com.wiki` → `com.axis`

#### axis-common/build.gradle
```gradle
// Update:
- Project name references
- Group ID: com.axis
```

#### axis-gateway/build.gradle
```gradle
// Update:
dependencies {
    implementation project(':axis-common')  // was :wiki-common
}
```

#### axis-media/build.gradle
```gradle
// Update:
dependencies {
    implementation project(':axis-common')  // was :wiki-common
}
```

### 3.4 Update Jib Configuration
Each service's build.gradle Jib section:
```gradle
jib {
    from {
        image = 'eclipse-temurin:21-jre'
    }
    to {
        image = "axis/${project.name}"  // was "wiki/${project.name}"
    }
}
```

---

## Phase 4: Rename Java Packages 📦

**Goal**: Rename all Java packages from com.wiki.* to com.axis.*.

### 4.1 axis-common Package Rename
```bash
# Current structure:
src/main/java/com/wiki/common/

# New structure:
src/main/java/com/axis/common/
```

**Steps:**
1. Rename directory: `com/wiki` → `com/axis`
2. Update all package declarations in .java files
3. Search and replace: `package com.wiki` → `package com.axis`
4. Search and replace: `import com.wiki` → `import com.axis`

### 4.2 axis-gateway Package Rename
```bash
# Current structure:
src/main/java/com/wiki/gateway/

# New structure:
src/main/java/com/axis/gateway/
```

**Steps:**
1. Rename directory: `com/wiki` → `com/axis`
2. Update all package declarations
3. Update all imports
4. Update application main class references

### 4.3 axis-media Package Rename
```bash
# Current structure:
src/main/java/com/wiki/media/

# New structure:
src/main/java/com/axis/media/
```

**Steps:**
1. Rename directory: `com/wiki` → `com/axis`
2. Update all package declarations
3. Update all imports
4. Update application main class references

### 4.4 Global Search and Replace

Use IDE or command-line tools:
```bash
# Find all references to com.wiki
git grep -l "com\.wiki"

# Find all wiki package references in config files
git grep -l "wiki" "*.yaml" "*.yml" "*.properties"
```

**Files likely to contain package references:**
- `application.yaml` (all services)
- `application.properties` (if exists)
- Test files
- MapStruct configuration
- Flyway configuration

---

## Phase 5: Update Application Configurations 📝

**Goal**: Update all Spring Boot configuration files.

### 5.1 axis-gateway/application.yaml
```yaml
# Update:
spring:
  application:
    name: axis-gateway  # was wiki-gateway
  cloud:
    gateway:
      routes:
        # Update route definitions for new service names
```

### 5.2 axis-media/application.yaml
```yaml
# Update:
spring:
  application:
    name: axis-media  # was wiki-media
  datasource:
    # Keep or update database name (decide on axis_media vs new name)
```

### 5.3 Database Names (Decision Required)
Options:
1. Keep current database names (less disruption)
2. Rename to axis_* (more consistency)

**Recommendation**: Update to `axis_media` for consistency

---

## Phase 6: Update Kubernetes Manifests ☸️

**Goal**: Update all K8s resources for new naming.

### 6.1 Update Namespace
**File**: `k8s/namespace.yaml`
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: axis  # was wiki
```

### 6.2 Update ConfigMaps
**File**: `k8s/config/configmaps.yaml`
```yaml
# Update:
- ConfigMap names: wiki-* → axis-*
- Data values: references to services
- Database names if changed
- Keycloak realm references
```

### 6.3 Update Secrets
**File**: `k8s/config/secrets.yaml`
```yaml
# Update:
- Secret names: wiki-* → axis-*
- Keep actual secret values unless rotating
```

### 6.4 Update Service Manifests

#### axis-gateway.yaml (was wiki-gateway.yaml)
```yaml
# File: k8s/services/axis-gateway.yaml
metadata:
  name: axis-gateway
  namespace: axis
spec:
  selector:
    app: axis-gateway
---
# Update Deployment name, labels, image reference
spec:
  template:
    spec:
      containers:
      - name: axis-gateway
        image: axis/gateway
```

#### axis-media.yaml (was wiki-media.yaml)
```yaml
# File: k8s/services/axis-media.yaml
metadata:
  name: axis-media
  namespace: axis
spec:
  selector:
    app: axis-media
---
# Update Deployment name, labels, image reference
spec:
  template:
    spec:
      containers:
      - name: axis-media
        image: axis/media
        env:
        - name: SPRING_APPLICATION_NAME
          value: "axis-media"
```

### 6.5 Delete Old Service Manifests
```bash
# Already handled in Phase 2, verify:
ls k8s/services/wiki-*.yaml  # should not exist
```

### 6.6 Update Infrastructure Manifests

**Files to review and update:**
- `k8s/infrastructure/postgres-app.yaml` - database names
- `k8s/infrastructure/keycloak-realm-config.yaml` - realm name
- `k8s/infrastructure/keycloak.yaml` - environment variables

---

## Phase 7: Update Keycloak Configuration 🔐

**Goal**: Change Keycloak realm from 'wiki' to 'axis'.

### 7.1 Update Realm Import Configuration
**File**: `k8s/infrastructure/keycloak-realm-config.yaml`

Update ConfigMap with new realm JSON:
```json
{
  "realm": "axis",
  "enabled": true,
  "clients": [
    {
      "clientId": "axis-backend",
      "secret": "secret",
      ...
    }
  ]
}
```

### 7.2 Update Service Configurations
All services with Keycloak integration:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: ${KEYCLOAK_JWK_SET_URI:http://keycloak:8080/realms/axis/protocol/openid-connect/certs}
```

### 7.3 Update ConfigMaps
**File**: `k8s/config/configmaps.yaml`
```yaml
data:
  KEYCLOAK_JWK_SET_URI: "http://keycloak:8080/realms/axis/protocol/openid-connect/certs"
```

### 7.4 Test Users Configuration
Update realm configuration:
- Admin user: `admin` / `admin` (roles: admin, user)
- Regular user: `user` / `user` (role: user)

---

## Phase 8: Update Skaffold Configuration 🚀

**Goal**: Update Skaffold for new artifact names and paths.

### 8.1 Update skaffold.yaml
```yaml
apiVersion: skaffold/v2beta2
kind: Config
metadata:
  name: axis-backend  # was wiki-backend

build:
  artifacts:
    - image: axis/gateway  # was wiki/gateway
      context: .
      jib:
        project: axis-gateway  # was wiki-gateway
        type: gradle
    - image: axis/media  # was wiki/media
      context: .
      jib:
        project: axis-media  # was wiki-media
        type: gradle
  local:
    push: false
    useBuildkit: true

deploy:
  kubectl:
    manifests:
      - k8s/namespace.yaml
      - k8s/config/configmaps.yaml
      - k8s/config/secrets.yaml
      - k8s/infrastructure/postgres-keycloak.yaml
      - k8s/infrastructure/postgres-app.yaml
      - k8s/infrastructure/keycloak.yaml
      - k8s/infrastructure/mongodb.yaml
      - k8s/infrastructure/rabbitmq.yaml
      - k8s/infrastructure/redis.yaml
      - k8s/infrastructure/keycloak-realm-config.yaml
      - k8s/services/axis-gateway.yaml
      - k8s/services/axis-media.yaml

portForward:
  - resourceType: service
    resourceName: axis-gateway  # was wiki-gateway
    namespace: axis  # was wiki
    port: 8080
    localPort: 8080
  - resourceType: service
    resourceName: keycloak
    namespace: axis  # was wiki
    port: 8080
    localPort: 8180
  - resourceType: service
    resourceName: rabbitmq
    namespace: axis
    port: 15672
    localPort: 15672
  - resourceType: service
    resourceName: postgres-app
    namespace: axis
    port: 5432
    localPort: 5433
  - resourceType: service
    resourceName: postgres-keycloak
    namespace: axis
    port: 5432
    localPort: 5434
  - resourceType: service
    resourceName: mongodb
    namespace: axis
    port: 27017
    localPort: 27017
  - resourceType: service
    resourceName: redis
    namespace: axis
    port: 6379
    localPort: 6379

profiles:
  - name: dev
    activation:
      - command: dev
    build:
      artifacts:
        - image: axis/gateway
          context: .
          jib:
            project: axis-gateway
            type: gradle
            args: ["--no-daemon"]
        - image: axis/media
          context: .
          jib:
            project: axis-media
            type: gradle
            args: ["--no-daemon"]
```

---

## Phase 9: Update Documentation 📚

**Goal**: Update all documentation to reflect new project structure.

### 9.1 Update README.md
```markdown
# Axis Backend Microservices

A microservices-based life goals planning platform (Trello-like) built with Spring Boot 3.4, Spring Cloud, and Keycloak authentication.

## Architecture

- **axis-gateway**: API Gateway (Spring Cloud Gateway) - Port 8080
- **axis-media**: Media management service
- **axis-common**: Shared library with common code

## Technology Stack

- Java 21
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
- Keycloak 24.0 (Identity Provider)
- PostgreSQL (for Keycloak and application data)
- MongoDB 7
- RabbitMQ 3
- Redis 7
- Kubernetes (Minikube)
- Skaffold

## Core Features

### Goal Management
- **Long-term goals**: Strategic life objectives (1-5+ years)
- **Medium-term goals**: Quarterly/yearly milestones (3-12 months)
- **Short-term goals**: Weekly/monthly tasks (days to weeks)

### Board System (Trello-like)
- Create multiple boards for different life areas
- Organize goals into customizable columns
- Drag-and-drop task management
- Progress tracking and visualization

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

# Deploy with Skaffold
skaffold dev

# Access services
# Gateway: http://localhost:8080
# Keycloak: http://localhost:8180
# RabbitMQ Management: http://localhost:15672
```

## Keycloak Setup

After deployment, Keycloak realm 'axis' is auto-configured:

1. Access Keycloak at http://localhost:8180
2. Realm: `axis`
3. Client: `axis-backend` (secret: `secret`)
4. Test users:
   - `admin` / `admin` (roles: admin, user)
   - `user` / `user` (role: user)

## Project Structure

```
axis-backend/
├── axis-common/         # Shared library
├── axis-gateway/        # API Gateway
├── axis-media/          # Media management service
└── k8s/                 # Kubernetes manifests
```

## API Endpoints

- `GET /api/goals/**` - Goal management
- `GET /api/boards/**` - Board management
- `GET /api/media/**` - Media upload/management

## Contributing

1. Create feature branch
2. Make changes
3. Test locally with Skaffold
4. Submit PR
```

### 9.2 Update CLAUDE.md
```markdown
# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Axis Backend is a microservices-based life goals planning platform (similar to Trello) built with Spring Boot 3.4, Spring Cloud, and Keycloak authentication. The system helps users manage long-term, medium-term, and short-term life goals using a board-based interface.

### Microservices

- **axis-gateway** (Port 8080): Spring Cloud Gateway, WebFlux-based reactive API gateway with OAuth2 JWT resource server
- **axis-media** (Port 8083): Media file management service with MongoDB storage
- **axis-common**: Shared library containing security utilities, exception handling, and DTOs

### Technology Stack

- Java 21 (use modern features: records, pattern matching, sealed classes)
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
- Keycloak 24.0 (realm: `axis`, client: `axis-backend`)
- PostgreSQL (separate instances for Keycloak and application data)
- MongoDB 7
- RabbitMQ 3
- Redis 7
- Kubernetes (Minikube) with Skaffold

## Domain Model

### Goals System
- **Long-term Goals**: Strategic objectives (1-5+ years)
- **Medium-term Goals**: Quarterly/yearly milestones (3-12 months)
- **Short-term Goals**: Daily/weekly tasks (days to weeks)

### Board System
- Trello-like boards for organizing goals
- Customizable columns/lists
- Card-based goal representation
- Progress tracking

## Build and Development Commands

[Keep existing commands, update service names]

## Architecture Patterns

[Keep existing patterns section]

### Authentication and Security

The platform uses Keycloak for OAuth2/OIDC authentication:

- **Gateway**: Uses WebFlux reactive security with JWT resource server
- **Services**: Standard Spring Security with JWT resource server
- **Keycloak realm**: `axis` (was `wiki`)
- **Client ID**: `axis-backend`

[Rest of CLAUDE.md with updated references]

## Common Pitfalls

1. **WebFlux vs WebMVC**: Gateway uses WebFlux (reactive), other services use standard WebMVC. Don't mix dependencies.
2. **Primary Keys**: Always use UUID, never Long/Integer for entity IDs
3. **Service Layer**: Always create interface first, then implementation
4. **Entity Exposure**: Never return entities from controllers, always use DTOs
5. **JPA ddl-auto**: Always use `validate`, never `update` or `create-drop`
6. **Keycloak URL**: Use internal service name `http://keycloak:8080` not `localhost:8180` in configs
7. **Namespace**: Always use `axis` namespace, not `wiki` or `default`
```

### 9.3 Clean Up IMPLEMENTATION_PLAN.md
Replace entire content with:
```markdown
# Axis Backend - Implementation Plan

**Project**: Axis Life Goals Planner
**Created**: 2026-01-02
**Status**: Planning Phase

---

## Project Overview

A microservices-based life goals planning platform with board-based organization, similar to Trello but focused on long-term, medium-term, and short-term life goals.

### Technology Stack
- **Backend**: Java 21, Spring Boot 3.4.1, Spring Cloud 2024.0.0
- **Security**: Keycloak 24.0, OAuth2, JWT
- **Databases**: PostgreSQL 16, MongoDB 7, Redis 7
- **Messaging**: RabbitMQ 3
- **Infrastructure**: Kubernetes (Minikube), Skaffold
- **Build**: Gradle, Jib

---

## Next Steps

This plan will be filled in after completing the migration from wiki-backend to axis-backend.

### Upcoming Features to Plan
- Goal management system (long-term, medium-term, short-term)
- Board/workspace management
- Task tracking and progress visualization
- Collaboration features
- Analytics and insights
- Mobile application support

---

**Last Updated**: 2026-01-02
**Status**: Awaiting migration completion
```

### 9.4 Update CHANGELOG.md
Add new entry at the top:
```markdown
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- **BREAKING**: Migrated from wiki-backend to axis-backend
- Renamed all modules: wiki-* → axis-*
- Renamed Java packages: com.wiki → com.axis
- Renamed Kubernetes namespace: wiki → axis
- Renamed Keycloak realm: wiki → axis
- Renamed Docker images: wiki/* → axis/*

### Removed
- **BREAKING**: Removed wiki-membership service
- **BREAKING**: Removed wiki-message service

### Added
- Migration plan documentation (MIGRATION_TO_AXIS.md)

[Keep existing changelog entries below]
```

### 9.5 Update .claudeignore (if needed)
Check if there are any wiki-specific paths to update:
```bash
# Update any references from wiki-* to axis-*
```

### 9.6 Update .mcp.json (if exists)
Update any database connection names:
```json
{
  "mcpServers": {
    "postgres-app": {
      "command": "uvx",
      "args": ["mcp-server-postgres", "postgresql://wiki_user:wiki_pass@localhost:5433/axis_media"]
    }
  }
}
```

---

## Phase 10: Update IDE and Build Files 🛠️

**Goal**: Update IDE-specific files and ensure clean build.

### 10.1 IntelliJ IDEA Configuration
If using IntelliJ:
- Reimport Gradle project
- Update run configurations (service names)
- Update module names in .idea folder (if committed)

### 10.2 Update .gitignore
Verify build directories:
```gitignore
# Verify these match new module names
axis-gateway/build/
axis-media/build/
axis-common/build/
```

### 10.3 Clean Build
```bash
# Clean all build artifacts
./gradlew clean

# Full rebuild
./gradlew clean build

# Build Docker images
./gradlew jibDockerBuild
```

---

## Phase 11: Testing and Verification ✅

**Goal**: Ensure everything works after migration.

### 11.1 Local Build Verification
```bash
# 1. Clean build
./gradlew clean build

# Expected: BUILD SUCCESSFUL

# 2. Verify module dependencies
./gradlew :axis-gateway:dependencies
./gradlew :axis-media:dependencies

# 3. Run tests
./gradlew test

# Expected: All tests pass
```

### 11.2 Docker Image Build
```bash
# Point to Minikube Docker
eval $(minikube docker-env)

# Build images
./gradlew jibDockerBuild

# Verify images created
docker images | grep axis

# Expected output:
# axis/gateway
# axis/media
```

### 11.3 Kubernetes Deployment Test
```bash
# Ensure Minikube is running
minikube status

# Deploy with Skaffold
skaffold dev

# Watch for errors during deployment
kubectl get pods -n axis -w

# Expected: All pods reach Running status
```

### 11.4 Service Health Checks
```bash
# Check gateway
curl http://localhost:8080/actuator/health

# Check media service (through gateway or port-forward)
kubectl port-forward -n axis service/axis-media 8083:8083
curl http://localhost:8083/actuator/health

# Expected: {"status":"UP"}
```

### 11.5 Keycloak Verification
```bash
# Access Keycloak admin console
open http://localhost:8180

# Verify:
# 1. Realm 'axis' exists
# 2. Client 'axis-backend' is configured
# 3. Test users exist (admin/admin, user/user)
# 4. Roles are configured (admin, user)

# Test token generation
curl -X POST http://localhost:8180/realms/axis/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=axis-backend" \
  -d "client_secret=secret" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin"

# Expected: Valid JWT token in response
```

### 11.6 Database Verification
```bash
# Connect to postgres-app
kubectl port-forward -n axis service/postgres-app 5433:5432

# Connect with psql
psql -h localhost -p 5433 -U axis_user -d axis_media

# Verify:
# 1. Database exists
# 2. Flyway migrations ran successfully
# 3. Tables are created

# Check Flyway history
SELECT * FROM flyway_schema_history;
```

### 11.7 Gateway Routing Test
```bash
# Test authenticated request through gateway
TOKEN=$(curl -s -X POST http://localhost:8180/realms/axis/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=axis-backend" \
  -d "client_secret=secret" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin" | jq -r '.access_token')

# Test media service through gateway
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/media/health

# Expected: Successful response
```

---

## Phase 12: Git Commit and Cleanup 🎯

**Goal**: Commit changes and clean up.

### 12.1 Review All Changes
```bash
# Check git status
git status

# Review changes
git diff

# Verify no unwanted files
git status --short
```

### 12.2 Stage Changes
```bash
# Add all changed files
git add -A

# Or stage selectively:
git add settings.gradle
git add skaffold.yaml
git add k8s/
git add README.md
git add CLAUDE.md
git add CHANGELOG.md
git add IMPLEMENTATION_PLAN.md
git add MIGRATION_TO_AXIS.md
# ... add other files
```

### 12.3 Commit Migration
```bash
git commit -m "Migrate wiki-backend to axis-backend

BREAKING CHANGE: Complete project restructuring

- Renamed project from wiki-backend to axis-backend
- Renamed all modules: wiki-* → axis-*
- Renamed Java packages: com.wiki.* → com.axis.*
- Renamed Kubernetes namespace: wiki → axis
- Renamed Keycloak realm: wiki → axis
- Renamed Docker images: wiki/* → axis/*

Removed services:
- wiki-membership
- wiki-message

Updated documentation:
- README.md
- CLAUDE.md
- IMPLEMENTATION_PLAN.md
- CHANGELOG.md

Added:
- MIGRATION_TO_AXIS.md (migration documentation)

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>"
```

### 12.4 Clean Up Old Artifacts
```bash
# Remove old Docker images (from Minikube)
eval $(minikube docker-env)
docker images | grep wiki | awk '{print $3}' | xargs docker rmi -f

# Clean Gradle cache (optional)
./gradlew clean
rm -rf ~/.gradle/caches/

# Delete old Kubernetes resources (if any exist)
kubectl delete namespace wiki --ignore-not-found=true
```

---

## Phase 13: Post-Migration Tasks 📋

**Goal**: Set up for future development.

### 13.1 Update CI/CD Pipelines
If GitHub Actions or other CI exists:
- Update workflow files (.github/workflows/)
- Update Docker registry references
- Update deployment scripts
- Update environment variables

### 13.2 Update Documentation Links
Search for any hardcoded URLs or references:
```bash
# Search for wiki references
git grep -i "wiki" | grep -v MIGRATION | grep -v CHANGELOG

# Fix any remaining references
```

### 13.3 Plan Next Features
Based on the new Axis platform purpose:
- [ ] Design Goal entity model
- [ ] Design Board/Workspace structure
- [ ] Plan API endpoints for goals
- [ ] Design frontend mockups
- [ ] Set up new database schemas

### 13.4 Update Team
- [ ] Notify team members of the migration
- [ ] Update development environment setup docs
- [ ] Share new access URLs
- [ ] Update bookmarks and shortcuts

---

## Rollback Plan 🔙

If migration fails, rollback using:

```bash
# Stop current deployment
skaffold delete

# Checkout previous state
git checkout backup-before-axis-migration

# Redeploy old version
eval $(minikube docker-env)
./gradlew clean jibDockerBuild
skaffold dev

# Or delete the feature branch
git branch -D feature/migrate-to-axis
git checkout master
```

---

## Estimated Time

| Phase | Estimated Time | Complexity |
|-------|---------------|------------|
| Phase 1: Preparation | 30 mins | Low |
| Phase 2: Delete Services | 30 mins | Low |
| Phase 3: Rename Modules | 1 hour | Medium |
| Phase 4: Rename Packages | 2 hours | High |
| Phase 5: Update Configs | 1 hour | Medium |
| Phase 6: Update K8s | 1.5 hours | Medium |
| Phase 7: Update Keycloak | 30 mins | Low |
| Phase 8: Update Skaffold | 30 mins | Low |
| Phase 9: Update Docs | 1.5 hours | Medium |
| Phase 10: Update Build | 30 mins | Low |
| Phase 11: Testing | 2 hours | High |
| Phase 12: Git Commit | 15 mins | Low |
| Phase 13: Post-Migration | 1 hour | Low |
| **Total** | **~12-14 hours** | **Medium-High** |

---

## Checklist Summary

### Critical Path
- [ ] Backup current state (git tag)
- [ ] Delete wiki-membership service completely
- [ ] Delete wiki-message service completely
- [ ] Rename Gradle modules (settings.gradle + directories)
- [ ] Rename Java packages (com.wiki → com.axis)
- [ ] Update all build.gradle files
- [ ] Update all application.yaml files
- [ ] Rename Kubernetes manifests
- [ ] Update namespace (wiki → axis)
- [ ] Update Keycloak realm configuration
- [ ] Update skaffold.yaml completely
- [ ] Update README.md
- [ ] Update CLAUDE.md
- [ ] Clean IMPLEMENTATION_PLAN.md
- [ ] Update CHANGELOG.md
- [ ] Build and test locally
- [ ] Deploy and verify on Minikube
- [ ] Test Keycloak authentication
- [ ] Test gateway routing
- [ ] Commit changes
- [ ] Clean up old artifacts

---

## Success Criteria

Migration is complete when:
1. ✅ All Gradle modules build successfully
2. ✅ All Docker images build with 'axis/' prefix
3. ✅ Skaffold deployment succeeds
4. ✅ All pods reach Running status in 'axis' namespace
5. ✅ Gateway accessible at http://localhost:8080
6. ✅ Keycloak accessible with 'axis' realm at http://localhost:8180
7. ✅ Authentication works (can get JWT token)
8. ✅ Gateway routing works (can call services)
9. ✅ Health checks pass for all services
10. ✅ All documentation updated
11. ✅ No references to 'wiki' remain (except in history/migration docs)
12. ✅ Tests pass

---

## Notes

- This migration is a **breaking change** - no backward compatibility
- Database schemas may need migration (decide on renaming)
- All existing data will need to be migrated if databases are renamed
- Keycloak users and clients will need to be recreated
- This is a good time to clean up technical debt
- Consider this a fresh start for the Axis platform

---

**Created by**: Claude Code
**Last Updated**: 2026-01-02
**Status**: Ready for execution