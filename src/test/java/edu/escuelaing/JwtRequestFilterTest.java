package edu.escuelaing;

import edu.escuelaing.security.JwtRequestFilter;
import edu.escuelaing.security.JwtValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
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

    private final JwtRequestFilter jwtRequestFilter = new JwtRequestFilter();
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

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
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTY5NTQ0Mzc2MiwiZXhwIjoxNjk1NDQzNzY1fQ.ebd17mGjNdYXTbnXWYvKGGysBk_45cWlLFWr-6M8hoE");
        Exception exception = assertThrows(JwtValidationException.ExpiredJwtException.class, () -> {
            jwtRequestFilter.doFilterInternal(request, response, filterChain);
        });
        assertEquals("El token JWT ha expirado", exception.getMessage());
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void whenRequestHasValidTokenThenRespondsOK() throws Exception {
        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTY5NTQ1NDk0NSwiZXhwIjoxNjk1NDU4NTQ1fQ.2UdXdygO_PDsOsSktZOG-fdnBV0U33m_Ao41JeS4tjI";
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