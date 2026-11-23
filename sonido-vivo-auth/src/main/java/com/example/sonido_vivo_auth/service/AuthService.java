
package com.example.sonido_vivo_auth.service;

import com.example.sonido_vivo_auth.dto.*;
import com.example.sonido_vivo_auth.model.Rol;
import com.example.sonido_vivo_auth.model.Usuario;
import com.example.sonido_vivo_auth.repository.UsuarioRepository;
import com.example.sonido_vivo_auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuariosRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuariosRepo.findByCorreo(request.correo())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!encoder.matches(request.password(), usuario.getContrasena())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        return new LoginResponse(
                usuario.getId(),
                jwt.generar(usuario),
                usuario.getCorreo(),
                usuario.getRol().name()
        );
    }

    public LoginResponse registrarCliente(RegistroRequest request) {
        if (usuariosRepo.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(request.correo());
        usuario.setContrasena(encoder.encode(request.password()));
        usuario.setRol(Rol.CLIENTE); // Asignación automática e inmodificable desde el registro público

        Usuario guardado = usuariosRepo.save(usuario);

        return new LoginResponse(
                guardado.getId(),
                jwt.generar(guardado),
                guardado.getCorreo(),
                guardado.getRol().name()
        );
    }

    public LoginResponse crearTrabajador(CrearTrabajadorRequest request) {
        if (request.rol() == Rol.CLIENTE) {
            throw new IllegalArgumentException("Este endpoint es exclusivo para asignar roles VENDEDOR o ADMINISTRADOR");
        }

        if (usuariosRepo.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado");
        }

        Usuario trabajador = new Usuario();
        trabajador.setCorreo(request.correo());
        trabajador.setContrasena(encoder.encode(request.password()));
        trabajador.setRol(request.rol());

        Usuario guardado = usuariosRepo.save(trabajador);

        return new LoginResponse(
                guardado.getId(),
                null, // No devolvemos token porque el admin está creando la cuenta para un tercero
                guardado.getCorreo(),
                guardado.getRol().name()
        );
    }
}
