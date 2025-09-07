package com.ing.brokerage.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expirationMs}")
    private long expirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        // in real app use a persistent strong key; here derive from secret bytes
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String subject, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public Claims validateAndGetClaims(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
