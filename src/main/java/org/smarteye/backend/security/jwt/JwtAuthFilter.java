package org.smarteye.backend.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Читает Authorization: Bearer ... и проставляет аутентификацию в SecurityContext.
 * Путь /api/v1/auth/**, Swagger, Actuator health и /ws/** пропускаются без проверки.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final Set<String> WHITELIST_PREFIXES = Set.of(
            "/api/v1/auth",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator/health",
            "/ws"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true; // CORS preflight
        return WHITELIST_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.startsWithIgnoreCase(auth, "Bearer ")) {
            String token = StringUtils.substringAfter(auth, " ");
            try {
                String username = jwtService.getUsername(token);
                var roles = jwtService.getRoles(token);
                var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (JwtException e) {
                log.debug("Invalid JWT: {}", e.getMessage());
                // Не устанавливаем аутентификацию — downstream вернёт 401 по правилам Security
            }
        }

        chain.doFilter(req, res);
    }
}
