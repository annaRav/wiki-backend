package com.axis.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility class for accessing security context information
 */
public class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    /**
     * Gets the current authenticated user's ID from JWT sub claim
     */
    public static Optional<String> getCurrentUserId() {
        return getCurrentJwt()
                .map(jwt -> jwt.getClaimAsString("sub"));
    }

    /**
     * Gets the current authenticated user's ID as UUID
     */
    public static Optional<UUID> getCurrentUserIdAsUUID() {
        return getCurrentUserId()
                .map(UUID::fromString);
    }

    /**
     * Gets the current authenticated user's email from JWT
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentJwt()
                .map(jwt -> jwt.getClaimAsString("email"));
    }

    /**
     * Gets the current authenticated user's preferred username
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentJwt()
                .map(jwt -> jwt.getClaimAsString("preferred_username"));
    }

    /**
     * Gets the current JWT token
     */
    public static Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.of(jwtAuth.getToken());
        }

        return Optional.empty();
    }

    /**
     * Gets all authorities/roles for the current user
     */
    public static Collection<String> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return java.util.Collections.emptyList();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * Checks if current user has a specific role
     */
    public static boolean hasRole(String role) {
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return getCurrentUserAuthorities().contains(roleWithPrefix);
    }

    /**
     * Checks if current user has any of the specified roles
     */
    public static boolean hasAnyRole(String... roles) {
        Collection<String> authorities = getCurrentUserAuthorities();

        for (String role : roles) {
            String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            if (authorities.contains(roleWithPrefix)) {
                return true;
            }
        }

        return false;
    }
}
