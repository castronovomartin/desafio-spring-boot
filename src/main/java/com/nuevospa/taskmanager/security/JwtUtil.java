package com.nuevospa.taskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

   private final SecretKey secretKey;
   private final long expirationMs;

   public JwtUtil(
         @Value("${jwt.secret}") String secret,
         @Value("${jwt.expiration-ms}") long expirationMs) {
      this.secretKey = Keys.hmacShaKeyFor(hexStringToByteArray(secret));
      this.expirationMs = expirationMs;
   }

   public String generateToken(String username) {
      return Jwts.builder()
                 .subject(username)
                 .issuedAt(new Date())
                 .expiration(new Date(System.currentTimeMillis() + expirationMs))
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
      } catch (Exception ex) {
         return false;
      }
   }

   private Claims parseClaims(String token) {
      return Jwts.parser()
                 .verifyWith(secretKey)
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();
   }

   private byte[] hexStringToByteArray(String hex) {
      int length = hex.length();
      byte[] data = new byte[length / 2];
      for (int i = 0; i < length; i += 2) {
         data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
               + Character.digit(hex.charAt(i + 1), 16));
      }
      return data;
   }
}
