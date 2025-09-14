package com.aut_jwt.aut_jwt.dto;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {
    String resolveToken(HttpServletRequest request); // extrae "Bearer <jwt>" → <jwt>
    boolean validateToken(String token);             // firma, expiración, issuer, aud, clockSkew
    Claims getAllClaims(String token);               // acceso a claims (si usa JJWT)
    Authentication getAuthentication(Claims claims); // crea Authentication (RBAC/mixto)
}
