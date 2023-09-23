package edu.escuelaing.security;

import edu.escuelaing.security.dto.AuthenticationResponse;
import edu.escuelaing.security.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        // Autenticar al usuario utilizando el AuthenticationManager
        System.out.println("AAAAAAAAAAAAA");

        // Generar un token JWT si la autenticación es exitosa
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String jwt = JwtUtil.generateToken(userDetails, expiration, secret);

        // Devolver el token JWT en la respuesta
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
