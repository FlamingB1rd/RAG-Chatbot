package com.tu.chatbot.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.security.jwt.secret}") String jwtSigningKey,
            @Value("${app.security.jwt.expiration-minutes:60}") long expirationMinutes
    ) {
        if (!StringUtils.hasText(jwtSigningKey)) {
            throw new IllegalArgumentException("JWT signing key must not be empty.");
        }
        this.signingKey = resolveSigningKey(jwtSigningKey);
        this.expirationMinutes = expirationMinutes;
    }

    public String generateJwt(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return buildToken(claims, userDetails.getUsername());
    }

    public long getExpirationInSeconds() {
        return expirationMinutes * 60;
    }

    public boolean isJwtValid(String jwt, UserDetails userDetails) {
        String userName = extractUserName(jwt);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(jwt);
    }

    public String extractUserName(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public List<String> extractRoles(String jwt) {
        List<?> roles = extractClaim(jwt, claims -> claims.get("roles", List.class));
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Object::toString)
                .toList();
    }

    private String buildToken(Map<String, Object> claims, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .and()
                .signWith(signingKey)
                .compact();
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private SecretKey resolveSigningKey(String signingKey) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(signingKey);
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("Base64 JWT key must decode to at least 256 bits.");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException ex) {
            byte[] keyBytes = signingKey.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT signing key must be at least 32 bytes.");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
}
