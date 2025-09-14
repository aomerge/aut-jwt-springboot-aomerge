package com.aut_jwt.aut_jwt.config.security;

import com.aut_jwt.aut_jwt.config.secrets.JwtProperties;
import com.aut_jwt.aut_jwt.dto.JwtTokenProvider;
import com.aut_jwt.aut_jwt.dto.jwt.UserPrincipal;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Component
public class JwtTokenProviderImp implements JwtTokenProvider {
    private  final JwtProperties jwtProperties;
    private String token;


    public JwtTokenProviderImp(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }
    @Override
    public boolean validateToken(String token) {
        Jwts.parserBuilder()
                .requireIssuer(jwtProperties.getIssuer())
                .setSigningKey(jwtProperties.getSecret())
                .build()
                .parseClaimsJws(token); // lanza JwtException si no es válido
        return true;
    }

    @Override
    public String resolveToken(HttpServletRequest request){
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String token = auth.substring(7).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }

    @Override
    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Authentication getAuthentication(Claims claims){
        // RBAC: roles desde "roles" (lista) o "role" (único)
                List<String> roles = Optional.ofNullable(claims.get("roles"))
                .map(v -> (v instanceof List<?> l) ? l.stream().map(String::valueOf).toList()
                        : List.of(String.valueOf(v)))
                .orElseGet(() -> {
                    String r = claims.get("role", String.class);
                    return r != null ? List.of(r) : List.of();
                });


        List<SimpleGrantedAuthority> authorities =
                roles.stream()
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        // ABAC/mixto: atributos en el principal (si los usa)
        Map<String, Object> attrs = Map.of(
                "department", claims.get("department"),
                "level",      claims.get("level"),
                "canDelete",  claims.get("canDelete"),
                "trusted",    claims.get("trusted")
        );
        var principal = new UserPrincipal(claims.getSubject(), attrs);

        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        return auth;
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = getAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true; // If there's an error parsing the token, consider it expired
        }
    }
}
