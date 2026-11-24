package com.example.sonido_vivo_pedido.security;

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
                String token = header.substring(7);
                Claims claims = jwtService.validar(token);

                String correo = claims.getSubject();
                String rol = claims.get("rol", String.class);

                if (correo != null && !correo.isBlank()
                        && rol != null
                        && (rol.equals("CLIENTE")
                        || rol.equals("VENDEDOR")
                        || rol.equals("ADMINISTRADOR"))) {

                    SimpleGrantedAuthority autoridad =
                            new SimpleGrantedAuthority("ROLE_" + rol);

                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    correo,
                                    null,
                                    List.of(autoridad)
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(autenticacion);
                } else {
                    SecurityContextHolder.clearContext();
                }

            } catch (JwtException | IllegalArgumentException ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}