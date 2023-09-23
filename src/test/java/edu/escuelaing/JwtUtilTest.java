package edu.escuelaing;

import edu.escuelaing.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"jwt.secret=dclVEBLJP7wBEXkGuWPM5PlwWPFCBjBtd8xPj0+71jk"})
public class JwtUtilTest {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Test
    public void testGenerateToken() {

        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, expiration, secret);

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
}
