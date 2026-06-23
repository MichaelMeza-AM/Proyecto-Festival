package com.festival.auth_service.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

@Service
public class HashService {
    
    public String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            try (Formatter formatter = new Formatter()) {
                for (byte b : md.digest()) {
                    formatter.format("%02x", b);
                }
                return formatter.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al procesar el hash", e);
        }
    }
}