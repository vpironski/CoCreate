package org.cocreate.CoCreate.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.cocreate.CoCreate.exception.SecurityException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
    private final String key = System.getenv("JWT_SECRET_KEY");

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    private final long EXPIRATION_TIME = 3600000 * 3;

    // Generate a JWT token with expiration time
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Extract username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration time from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract specific claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new SecurityException("Invalid JWT token: " + e.getMessage());
        }
    }

//     Check if the token has expired
    private boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration == null || expiration.before(new Date());
    }

    // Validate the token (check if it's valid and matches the user)
    public boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return (tokenUsername != null &&
                    tokenUsername.equals(username) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}

