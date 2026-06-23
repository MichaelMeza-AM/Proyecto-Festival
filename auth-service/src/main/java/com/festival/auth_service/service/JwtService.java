package com.festival.auth_service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

   // CAMBIO 1: Ahora recibe el Long id
    public String generateToken(Long id, String email, String role) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(String.valueOf(id)) // CAMBIO 2: Guardamos el ID como principal
                .claim("email", email)          // CAMBIO 3: El email va como dato extra
                .claim("rol", role) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) 
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
}