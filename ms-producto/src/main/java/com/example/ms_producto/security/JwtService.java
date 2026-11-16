
package com.example.ms_producto.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    // Obtener la clave utilizada para validar la firma
    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Obtener los datos del token y verificar su firma
    public Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Obtener el correo del usuario
    public String obtenerCorreo(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Obtener el rol del usuario
    public String obtenerRol(String token) {
        return obtenerClaims(token).get("rol", String.class);
    }

    // Validar el token
    public boolean validarToken(String token) {
        try {
            Claims claims = obtenerClaims(token);

            return claims.getSubject() != null
                    && !claims.getSubject().isBlank()
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new java.util.Date())
                    && claims.get("rol", String.class) != null;

        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
