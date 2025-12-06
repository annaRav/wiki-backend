# Wiki Auth Skills

## Overview
Backend for Frontend (BFF) authentication service using WebFlux.

## Core Responsibilities
- OAuth2 authentication flow with Keycloak
- Token management (access, refresh)
- User session management
- Keycloak Admin API integration
- User registration and profile management

## Technical Skills Required
- Spring WebFlux
- OAuth2 Client
- Reactive WebClient
- Keycloak Admin Client

## Keycloak Integration
- Realm: `wiki`
- Client: `wiki-backend`
- Admin operations via admin-cli

## Endpoints (To Be Implemented)
- `POST /api/auth/login` - Initiate OAuth2 flow
- `POST /api/auth/logout` - Invalidate session
- `POST /api/auth/refresh` - Refresh access token
- `GET /api/auth/profile` - Get current user profile
- `POST /api/auth/register` - Register new user

## Future Enhancements
- [ ] Social login (Google, GitHub)
- [ ] Two-factor authentication
- [ ] Password reset flow
- [ ] Email verification
