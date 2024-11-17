package org.example.backend.global.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private SecretKey secretKey;
    private final Long ACCESS_EXPIRE = 1000L * 60 * 60; // 1시간
    private final Long REFRESH_EXPIRE = 1000L * 60 * 60 * 24 * 30; // 30일

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Long getIdx(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("idx", Long.class);
    }

    public Boolean isValid(String token) {
        try{
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().after(new Date());
        } catch (ExpiredJwtException | SignatureException jwtException){
            return false;
        }
    }

    public Boolean isExpired(String token) {
        try{
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException expiredJwtException){
            return true;
        }
    }

    public String createAccessToken(Long idx, String email, String role) {
        return Jwts.builder()
                .claim("idx", idx)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long idx, String email, String role) {
        return Jwts.builder()
                .claim("idx", idx)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE))
                .signWith(secretKey)
                .compact();
    }
}
