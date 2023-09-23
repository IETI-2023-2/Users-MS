package edu.escuelaing.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private static String secret = "dclVEBLJP7wBEXkGuWPM5PlwWPFCBjBtd8xPj0+71jk";

    @Value("${jwt.expiration}")
    private Long expiration = 3L;

    public static String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException ex) {
            // Manejo de excepción de token expirado
            throw new JwtValidationException.ExpiredJwtException("El token JWT ha expirado");
        } catch (Exception e) {
            // Manejo de excepción de firma JWT inválida u otras excepciones
            throw new JwtValidationException.InvalidJwtSignatureException("Firma JWT inválida");
        }
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Boolean isTokenExpired(String token) {
        final Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    public static String generateToken(UserDetails userDetails, Long expiration, String secret) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), expiration, secret);
    }

    private static String createToken(Map<String, Object> claims, String subject, Long expiration, String secret) {
        final Date now = new Date();
        final Date expirationDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public static boolean isTokenValid(String token, UserDetails userDetails) {
        try {

            if (token == null || token.isEmpty()) {
                return false;
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!userDetails.getUsername().equals(claims.getSubject())) {
                return false;
            }

            if (isTokenExpired(token)) {
                return false;
            }

            return true;
        } catch (ExpiredJwtException ex) {
            throw new JwtValidationException.ExpiredJwtException("El token JWT ha expirado");
        } catch (Exception e) {
            throw new JwtValidationException.InvalidJwtSignatureException("Firma JWT inválida");
        }
    }
}

