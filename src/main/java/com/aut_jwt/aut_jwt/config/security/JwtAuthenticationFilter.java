package com.aut_jwt.aut_jwt.config.security;

import com.aut_jwt.aut_jwt.dto.JwtTokenProvider;
import io.jsonwebtoken.*;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProviderImp tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String[] WHITELIST = {
            "/auth/**",
            "/openapi.yaml",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/actuator/health"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getServletPath(); // mejor que getRequestURI()
        for (String pattern : WHITELIST) {
            if (PATH_MATCHER.match(pattern, path)) return true;
        }
        // Omite preflight CORS si aplica
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain ) throws ServletException, IOException {

        String token = tokenProvider.resolveToken(request);
        try {
            if (token == null || !tokenProvider.validateToken(token) || !tokenProvider.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            // 2) Obtener claims una sola vez
            Claims claims = tokenProvider.getAllClaims(token);

            // 3) Construir Authentication (RBAC/mixto/ABAC según su diseño)
            Authentication auth = tokenProvider.getAuthentication(claims);

            // 4) Poblar el contexto
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
