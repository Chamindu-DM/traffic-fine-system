package com.trafficfine.common.security;

/**
 * Shared JWT constants used across the API Gateway and downstream services.
 * The gateway validates the JWT and propagates user identity via these headers.
 */
public final class JwtConstants {

    private JwtConstants() {
        // Utility class — prevent instantiation
    }

    /** HTTP header carrying the JWT token. */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** Prefix for Bearer tokens. */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** Downstream header for the authenticated user's ID. */
    public static final String HEADER_USER_ID = "X-User-Id";

    /** Downstream header for the authenticated user's username. */
    public static final String HEADER_USERNAME = "X-Username";

    /** Downstream header for the authenticated user's role. */
    public static final String HEADER_USER_ROLE = "X-User-Role";

    /** JWT claim key for user ID. */
    public static final String CLAIM_USER_ID = "userId";

    /** JWT claim key for role. */
    public static final String CLAIM_ROLE = "role";

    /** JWT claim key for username (subject). */
    public static final String CLAIM_USERNAME = "sub";
}
