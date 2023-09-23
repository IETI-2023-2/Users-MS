package edu.escuelaing.security;

public class JwtValidationException extends RuntimeException {

    public JwtValidationException(String message) {
        super(message);
    }

    public static class ExpiredJwtException extends JwtValidationException {
        public ExpiredJwtException(String message) {
            super(message);
        }
    }

    public static class InvalidJwtSignatureException extends JwtValidationException {
        public InvalidJwtSignatureException(String message) {
            super(message);
        }
    }
}

