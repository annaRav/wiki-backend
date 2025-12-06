# Wiki Organization Skills

## Overview
Organization and role management service with PostgreSQL.

## Core Responsibilities
- Organization CRUD operations
- Role and permission management
- User-role assignments
- Organization membership management
- Access control and authorization

## Technical Skills Required
- Spring Data JPA
- PostgreSQL
- Flyway migrations
- MapStruct for DTO mapping
- OAuth2 Resource Server

## Domain Model
- **Organization**: Main entity with slug, name, description
- **Role**: Organization-specific roles
- **Permission**: Granular permissions (org.read, role.write, etc.)
- **RolePermission**: Many-to-many mapping
- **UserRole**: User assignments to roles within organizations

## Endpoints (To Be Implemented)
- `GET /api/organizations` - List organizations
- `POST /api/organizations` - Create organization
- `GET /api/organizations/{slug}` - Get organization details
- `PUT /api/organizations/{slug}` - Update organization
- `DELETE /api/organizations/{slug}` - Delete organization
- `GET /api/organizations/{slug}/roles` - List roles
- `POST /api/organizations/{slug}/roles` - Create role
- `POST /api/organizations/{slug}/members` - Add member
- `GET /api/organizations/{slug}/members` - List members

## Authorization Rules
- Organization admin can manage all aspects
- Role-based permissions for operations
- System roles cannot be deleted

## Future Enhancements
- [ ] Organization hierarchies (parent/child)
- [ ] Custom permission definitions
- [ ] Audit logging for role changes
- [ ] Invitation system with email
- [ ] Organization settings and preferences
