package edu.escuelaing;

import edu.escuelaing.security.JwtRequestFilter;
import edu.escuelaing.security.JwtUtil;
import edu.escuelaing.security.JwtValidationException;
import edu.escuelaing.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.data.mongodb.uri=mongodb://localhost/testdb", "jwt.secret=dclVEBLJP7wBEXkGuWPM5PlwWPFCBjBtd8xPj0+71jk"})
public class JwtRequestFilterTest {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Test
    public void whenRequestHasNoAuthorizationHeaderThenRespondsUnauthorizedStatus() throws Exception {
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenRequestHasNoBearerTokenThenRespondsUnauthorizedStatus() throws Exception {
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Token");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenRequestHasInvalidTokenThenRespondsUnauthorized() throws Exception {
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("USER")
                .build();

        String token = JwtUtil.generateToken(userDetails, 0L, secret);

        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer "+ token);
        Exception exception = assertThrows(JwtValidationException.ExpiredJwtException.class, () -> {
            jwtRequestFilter.doFilterInternal(request, response, filterChain);
        });
        assertEquals("El token JWT ha expirado", exception.getMessage());
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void whenRequestHasValidTokenThenRespondsOK() throws Exception {
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("USER")
                .build();

        String validToken = JwtUtil.generateToken(userDetails, expiration, secret);

        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void whenAuthRequestTheDoFilterIsCalled() throws Exception {
        when(request.getRequestURI()).thenReturn("v1/auth");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

}