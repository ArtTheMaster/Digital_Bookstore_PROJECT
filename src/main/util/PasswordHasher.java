package main.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {

    public static String hash(String rawPassword) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(rawPassword.getBytes());
            
            return Base64.getEncoder().encodeToString(salt) + ":" + 
                   Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) { 
            throw new RuntimeException("Failed to hash password", e); 
        }
    }

    public static boolean verify(String rawPassword, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] actualHash = md.digest(rawPassword.getBytes());
            
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) { 
            return false; 
        }
    }
}