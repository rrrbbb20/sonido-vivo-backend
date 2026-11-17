
package com.example.ms_producto.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            try {
                // Extraer el token sin la palabra Bearer
                String token = header.substring(7);

                // Validar el token y obtener sus datos
                Claims claims = jwtService.validar(token);

                String correo = claims.getSubject();
                String rol = claims.get("rol", String.class);

                // Comprobar que el usuario tenga un rol permitido
                if (correo != null && !correo.isBlank()
                        && rol != null
                        && (rol.equals("CLIENTE")
                        || rol.equals("VENDEDOR")
                        || rol.equals("ADMINISTRADOR"))) {

                    // Convertir el rol al formato de Spring Security
                    SimpleGrantedAuthority autoridad =
                            new SimpleGrantedAuthority("ROLE_" + rol);

                    // Crear la autenticación del usuario
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    correo,
                                    null,
                                    List.of(autoridad)
                            );

                    // Registrar al usuario como autenticado
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                } else {
                    SecurityContextHolder.clearContext();
                }

            } catch (JwtException | IllegalArgumentException e) {

                // Si el token es inválido, limpiar la autenticación
                SecurityContextHolder.clearContext();
            }
        }

        // Continuar con la siguiente etapa de seguridad
        filterChain.doFilter(request, response);
    }
}
