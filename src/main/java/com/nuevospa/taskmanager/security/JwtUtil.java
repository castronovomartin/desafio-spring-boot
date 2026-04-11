package com.nuevospa.taskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;

@Slf4j
@Component
public class JwtUtil {

   private final SecretKey secretKey;
   private final long expirationMs;

   public JwtUtil(
         @Value("${jwt.secret}") String secret,
         @Value("${jwt.expiration-ms}") long expirationMs) {
      this.secretKey = Keys.hmacShaKeyFor(HexFormat.of().parseHex(secret));
      this.expirationMs = expirationMs;
   }

   public String generateToken(String username) {
      Instant now = Instant.now();
      return Jwts.builder()
                 .subject(username)
                 .issuedAt(Date.from(now))
                 .expiration(Date.from(now.plusMillis(expirationMs)))
                 .signWith(secretKey)
                 .compact();
   }

   public String extractUsername(String token) {
      return parseClaims(token).getSubject();
   }

   public boolean validateToken(String token) {
      try {
         parseClaims(token);
         return true;
      } catch (ExpiredJwtException ex) {
         log.warn("JWT token expired: {}", ex.getMessage());
      } catch (JwtException ex) {
         log.warn("JWT token invalid: {}", ex.getMessage());
      } catch (Exception ex) {
         log.warn("JWT validation error: {}", ex.getMessage());
      }
      return false;
   }

   private Claims parseClaims(String token) {
      return Jwts.parser()
                 .verifyWith(secretKey)
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();
   }

}
