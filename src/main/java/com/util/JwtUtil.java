package com.util;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	
	public static void main(String[] args) {
//		String username =
//			    JwtUtil.getUsername("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2YW5jZSIsImlhdCI6MTc4MjEyMjk2NywiZXhwIjoxNzgyMjA5MzY3fQ.TG3vYP2sVe2PYAEskBPXqtJjUIdxFeQQ3UFhNlQz5jA");
//				System.out.println(username);
	}

    private static final String SECRET =
            "stocktracker-secret-key-stocktracker-2026";

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes());

    public static String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000))
                .signWith(KEY)
                .compact();
    }
    
    public static String getUsername(
            String token) {

        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}