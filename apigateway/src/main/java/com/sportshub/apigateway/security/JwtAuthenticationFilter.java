package com.sportshub.apigateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain
    ) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = jwtService.validateAndGetClaims(token);

            String role = claims.get("role", String.class);

            if (!isAuthorized(path, role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            var mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", String.valueOf(claims.get("userId")))
                    .header("X-User-Email", claims.get("email", String.class))
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception exception) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicPath(String path) {
        return path.equals("/api/user/auth/login")
                || path.equals("/api/user/auth/register")
                || path.startsWith("/actuator/health");
    }

    private boolean isAuthorized(String path, String role) {
        if ("ADMIN".equals(role)) {
            return true;
        }

        if (path.startsWith("/api/user/users") || path.startsWith("/api/user/roles")) {
            return false;
        }

        if (path.startsWith("/api/analytics")) {
            return "MANAGER".equals(role) || "ANALYST".equals(role);
        }

        if (path.startsWith("/api/facility") && isWriteOperation(path)) {
            return "MANAGER".equals(role);
        }

        return "USER".equals(role) || "MANAGER".equals(role) || "ANALYST".equals(role);
    }

    private boolean isWriteOperation(String path) {
        return true;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}