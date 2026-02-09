package com.axis.common.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Utility class for accessing security context information.
 * Uses MicroProfile JWT API - compatible with Quarkus.
 */
@ApplicationScoped
public class SecurityUtils {

    @Inject
    JsonWebToken jwt;

    /**
     * Gets the current authenticated user's ID from JWT sub claim
     */
    public Optional<String> getCurrentUserId() {
        if (jwt == null || jwt.getSubject() == null) {
            return Optional.empty();
        }
        return Optional.of(jwt.getSubject());
    }

    /**
     * Gets the current authenticated user's ID as UUID
     */
    public Optional<UUID> getCurrentUserIdAsUUID() {
        return getCurrentUserId().map(UUID::fromString);
    }

    /**
     * Gets the current authenticated user's email from JWT
     */
    public Optional<String> getCurrentUserEmail() {
        if (jwt == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(jwt.getClaim("email"));
    }

    /**
     * Gets the current authenticated user's preferred username
     */
    public Optional<String> getCurrentUsername() {
        if (jwt == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(jwt.getClaim("preferred_username"));
    }

    /**
     * Gets all groups/roles for the current user
     */
    public Set<String> getCurrentUserGroups() {
        if (jwt == null) {
            return Set.of();
        }
        return jwt.getGroups();
    }

    /**
     * Checks if current user has a specific role
     */
    public boolean hasRole(String role) {
        if (jwt == null) {
            return false;
        }
        return jwt.getGroups().contains(role);
    }

    /**
     * Checks if current user has any of the specified roles
     */
    public boolean hasAnyRole(String... roles) {
        if (jwt == null) {
            return false;
        }
        Set<String> userGroups = jwt.getGroups();
        for (String role : roles) {
            if (userGroups.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a custom claim from the JWT
     */
    public <T> Optional<T> getClaim(String claimName) {
        if (jwt == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(jwt.getClaim(claimName));
    }
}
