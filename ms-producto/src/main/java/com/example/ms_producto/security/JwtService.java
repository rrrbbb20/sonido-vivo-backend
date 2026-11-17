
package com.example.ms_producto.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    // Obtener la clave secreta para verificar el JWT
    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // Validar el token y obtener sus datos
    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
