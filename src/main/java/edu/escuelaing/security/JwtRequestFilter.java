package edu.escuelaing.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    String MISSING_TOKEN_ERROR_MESSAGE = "Missing or wrong token";

    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter() {
        setJwtUtil();
    }

    @Autowired
    public void setUserDetailsService() {
        this.userDetailsService = username -> null;
    }

    @Autowired
    public void setJwtUtil() {
        this.jwtUtil = new JwtUtil();
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
            System.out.println("Username: " + username);
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UserDetails userDetails = User.withUsername("testUser")
                    .password("password")
                    .roles("USER")
                    .build();

            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("TOKEN_EXPIRED_MALFORMED_ERROR_MESSAGE");
                return;
            }
        }
        response.setStatus(HttpStatus.OK.value());
        filterChain.doFilter(request, response);
    }
}
