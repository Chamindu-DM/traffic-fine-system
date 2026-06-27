package com.trafficfine.common.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Holds the authenticated user context extracted from gateway-injected headers.
 * Downstream services use this instead of parsing JWT themselves — the gateway
 * has already validated the token and set X-User-Id, X-Username, X-User-Role.
 */
public record UserContext(
        String userId,
        String username,
        String role
) {

    /**
     * Extract UserContext from the incoming request headers set by the API Gateway.
     *
     * @param request the HTTP servlet request
     * @return a populated UserContext, or one with null fields if headers are missing
     */
    public static UserContext fromRequest(HttpServletRequest request) {
        return new UserContext(
                request.getHeader(JwtConstants.HEADER_USER_ID),
                request.getHeader(JwtConstants.HEADER_USERNAME),
                request.getHeader(JwtConstants.HEADER_USER_ROLE)
        );
    }

    /**
     * Check whether this context represents an authenticated admin user.
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    /**
     * Check whether this context has any authenticated user.
     */
    public boolean isAuthenticated() {
        return userId != null && !userId.isBlank();
    }
}
