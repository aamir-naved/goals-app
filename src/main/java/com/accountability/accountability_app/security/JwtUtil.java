package com.accountability.accountability_app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey key;

    @Value("${jwt.expiration}") // Expiration time in milliseconds (e.g., 86400000 for 1 day)
    private long jwtExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long.");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        System.out.println("🔑 JWT Secret Key initialized.");
    }

    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Add expiration
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        System.out.println("✅ Generated Token for " + username + ": " + token);
        return token;
    }

    public String extractEmail(String token) {
        try {
            String email = extractClaim(token, Claims::getSubject);
            System.out.println("📧 Extracted Email from Token: " + email);
            return email;
        } catch (Exception e) {
            System.out.println("❌ Failed to extract email: " + e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            boolean isExpired = isTokenExpired(token);

            if (extractedEmail == null) {
                System.out.println("❌ Token validation failed: Unable to extract email.");
                return false;
            }

            if (!email.equals(extractedEmail)) {
                System.out.println("❌ Token validation failed: Email does not match.");
                return false;
            }

            if (isExpired) {
                System.out.println("❌ Token validation failed: Token is expired.");
                return false;
            }

            System.out.println("✅ Token is valid for email: " + email);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("⚠️ Token expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("⚠️ Malformed Token: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("⚠️ Invalid Token Signature: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️ Other Token validation error: " + e.getMessage());
        }
        return false; // Catch all errors and return false
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        boolean expired = expiration != null && expiration.before(new Date());
        System.out.println(expired ? "❌ Token is expired" : "✅ Token is not expired");
        return expired;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            System.out.println("❌ Error extracting claims: " + e.getMessage());
            throw e;
        }
    }
}
