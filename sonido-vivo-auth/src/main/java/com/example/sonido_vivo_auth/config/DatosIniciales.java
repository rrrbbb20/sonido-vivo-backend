package com.example.sonido_vivo_auth.config;

import com.example.sonido_vivo_auth.model.Rol;
import com.example.sonido_vivo_auth.model.Usuario;
import com.example.sonido_vivo_auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner usuarioInicial(
            UsuarioRepository repo,
            PasswordEncoder encoder,
            @Value("${app.admin.correo}") String adminCorreo,
            @Value("${app.admin.password}") String adminPassword) {

        return args -> {
            if (repo.findByCorreo(adminCorreo).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setCorreo(adminCorreo);
                admin.setContrasena(encoder.encode(adminPassword));
                admin.setRol(Rol.ADMINISTRADOR);
                repo.save(admin);
            }
        };
    }
}