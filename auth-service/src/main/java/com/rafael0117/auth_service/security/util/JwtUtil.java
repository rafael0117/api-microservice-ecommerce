package com.rafael0117.auth_service.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private String buildToken(Map<String, Object> claims, String subject, long ttl, String typ) {
        claims = new HashMap<>(claims);
        claims.put("typ", typ); // "access" | "refresh"
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(UserDetails user) {
        Map<String, Object> claims = Map.of(
                "roles", user.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        );
        return buildToken(claims, user.getUsername(), accessExpirationMs, "access");
    }

    public String generateRefreshToken(UserDetails user) {
        return buildToken(Collections.emptyMap(), user.getUsername(), refreshExpirationMs, "refresh");
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) { return extractAllClaims(token).getSubject(); }

    public Date extractExpiration(String token) { return extractAllClaims(token).getExpiration(); }

    public String extractType(String token) {
        Object typ = extractAllClaims(token).get("typ");
        return typ != null ? typ.toString() : "access";
    }

    public boolean validateToken(String token) {
        try { return extractExpiration(token).after(new Date()); }
        catch (Exception e) { return false; }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object rolesObject = extractAllClaims(token).get("roles");
        if (rolesObject instanceof List<?> rolesList) {
            return rolesList.stream().map(Object::toString).toList();
        }
        return List.of();
    }
}