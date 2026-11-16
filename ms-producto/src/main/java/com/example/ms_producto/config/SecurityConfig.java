
package com.example.ms_producto.config;


import com.example.ms_producto.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        return http
                // Desactivar CSRF para nuestra API REST
                .csrf(csrf -> csrf.disable())

                // Habilitar CORS
                .cors(Customizer.withDefaults())

                // No utilizar sesiones en el servidor
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Configurar los permisos de los endpoints
                .authorizeHttpRequests(auth -> auth

                        // Permitir solicitudes CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Permitir acceder a Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Consultar productos: acceso público
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/productos",
                                "/api/v1/productos/**"
                        ).permitAll()

                        // Registrar productos: vendedor o administrador
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/productos"
                        ).hasAnyRole("VENDEDOR", "ADMINISTRADOR")

                        // Actualizar productos: vendedor o administrador
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/productos/*"
                        ).hasAnyRole("VENDEDOR", "ADMINISTRADOR")

                        // Eliminar productos: solo administrador
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/productos/*"
                        ).hasRole("ADMINISTRADOR")

                        // Bloquear cualquier otra ruta
                        .anyRequest().denyAll()
                )

                // Responder 401 si falta autenticación
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                )

                // Ejecutar nuestro filtro JWT
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    // Configuración CORS para React
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
