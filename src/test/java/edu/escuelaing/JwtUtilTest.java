package edu.escuelaing;

import edu.escuelaing.security.JwtUtil;
import edu.escuelaing.security.JwtValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"jwt.secret=dclVEBLJP7wBEXkGuWPM5PlwWPFCBjBtd8xPj0+71jk"})
public class JwtUtilTest {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Test
    public void GenerateToken() {

        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, 3600L, secret);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        System.out.println(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(userDetails.getUsername(), claims.getSubject());
    }

    @Test
    public void ExtractUsernameFromValidToken() {
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, expiration, secret);

        String username = JwtUtil.extractUsername(token);

        assertEquals("testUser", username);
    }

    @Test
    public void ExtractUsernameFromInvalidToken() {
        assertThrows(JwtValidationException.InvalidJwtSignatureException.class, () -> JwtUtil.extractUsername("invalid_token"));
    }

    @Test
    public void ExtractExpirationFromValidToken() {
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, expiration, secret);

        Date expirationDate = JwtUtil.extractExpiration(token);

        assertNotNull(expirationDate);
    }

    @Test
    public void IsTokenExpiredWithValidToken() {
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, expiration, secret);

        assertFalse(JwtUtil.isTokenExpired(token));
    }

    @Test
    public void IsTokenExpiredWithExpiredToken() {
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, 1L, secret);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThrows(ExpiredJwtException.class, () -> JwtUtil.isTokenExpired(token));
    }

    @Test
    public void IsTokenValidWithValidToken() {
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, expiration, secret);

        assertTrue(JwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    public void IsTokenValidWithInvalidToken() {
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, expiration, secret);

        assertThrows(JwtValidationException.InvalidJwtSignatureException.class, () -> JwtUtil.isTokenValid("invalid_token", userDetails));
    }
}
