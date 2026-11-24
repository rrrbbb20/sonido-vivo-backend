package com.example.sonido_vivo_cliente.service;

import com.example.sonido_vivo_cliente.client.AuthClient;
import com.example.sonido_vivo_cliente.dto.*;
import com.example.sonido_vivo_cliente.exception.RecursoNoEncontradoException;
import com.example.sonido_vivo_cliente.model.Cliente;
import com.example.sonido_vivo_cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuthClient authClient;

    // Registrar un nuevo cliente (orquesta Auth + BD Clientes)
    @Transactional
    public ClienteRegistroResponse registrar(ClienteRegistroRequest request) {
        String rutLimpio = request.getRut().trim().toUpperCase();
        String correoLimpio = request.getCorreo().trim().toLowerCase();

        log.info("Iniciando registro de cliente con RUT: {}", rutLimpio);

        if (clienteRepository.existsByRutIgnoreCase(rutLimpio)) {
            log.warn("Intento de registro con RUT duplicado: {}", rutLimpio);
            throw new IllegalStateException("El RUT ya se encuentra registrado");
        }

        AuthRegistroResponse authResponse = authClient.registrarUsuario(correoLimpio, request.getPassword());

        if (authResponse == null || authResponse.idUsuario() == null) {
            throw new IllegalStateException("No se recibió el ID del usuario desde Auth");
        }

        Cliente cliente = new Cliente();
        cliente.setUsuarioId(authResponse.idUsuario());
        cliente.setNombre(request.getNombre().trim());
        cliente.setApellido(request.getApellido().trim());
        cliente.setRut(rutLimpio);
        cliente.setTelefono(request.getTelefono().trim());
        cliente.setFechaRegistro(LocalDateTime.now());

        Cliente guardado = clienteRepository.save(cliente);

        log.info("Cliente registrado correctamente. ID: {}, usuarioId: {}", guardado.getId(), guardado.getUsuarioId());

        return mapToRegistroResponse(guardado, authResponse);
    }

    // Listar todos los clientes (solo ADMINISTRADOR)
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        log.info("Listando todos los clientes");

        return clienteRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Buscar cliente por ID propio
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id, Authentication auth) {
        log.info("Buscando cliente con ID: {}", id);

        Cliente cliente = buscarEntidadPorId(id);
        validarPropietarioOAdmin(cliente, auth);

        return mapToResponse(cliente);
    }

    // Buscar cliente por ID de usuario (Auth)
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorUsuarioId(Long usuarioId, Authentication auth) {
        log.info("Buscando cliente con usuarioId: {}", usuarioId);

        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró perfil de cliente para el usuario ID: " + usuarioId
                ));

        validarPropietarioOAdmin(cliente, auth);

        return mapToResponse(cliente);
    }

    // Actualizar datos de perfil
    @Transactional
    public ClienteResponse actualizar(Long id, ActualizarClienteRequest request, Authentication auth) {
        log.info("Actualizando cliente con ID: {}", id);

        Cliente cliente = buscarEntidadPorId(id);
        validarPropietarioOAdmin(cliente, auth);

        cliente.setNombre(request.getNombre().trim());
        cliente.setApellido(request.getApellido().trim());
        cliente.setTelefono(request.getTelefono().trim());

        Cliente actualizado = clienteRepository.save(cliente);

        log.info("Cliente actualizado correctamente. ID: {}", actualizado.getId());

        return mapToResponse(actualizado);
    }

    private void validarPropietarioOAdmin(Cliente cliente, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        Long idUsuarioToken = (auth.getDetails() instanceof Long id) ? id : null;

        if (!esAdmin && (idUsuarioToken == null || !idUsuarioToken.equals(cliente.getUsuarioId()))) {
            log.warn("Acceso denegado: usuario ID {} intentó acceder al perfil de usuario ID {}",
                    idUsuarioToken, cliente.getUsuarioId());
            throw new AccessDeniedException("No tienes permisos para acceder o modificar este perfil");
        }
    }

    private Cliente buscarEntidadPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el cliente con ID: " + id
                ));
    }

    private ClienteRegistroResponse mapToRegistroResponse(Cliente cliente, AuthRegistroResponse auth) {
        return ClienteRegistroResponse.builder()
                .id(cliente.getId())
                .usuarioId(cliente.getUsuarioId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .rut(cliente.getRut())
                .telefono(cliente.getTelefono())
                .correo(auth.correo())
                .rol(auth.rol())
                .token(auth.token())
                .fechaRegistro(cliente.getFechaRegistro())
                .build();
    }

    private ClienteResponse mapToResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .usuarioId(cliente.getUsuarioId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .rut(cliente.getRut())
                .telefono(cliente.getTelefono())
                .fechaRegistro(cliente.getFechaRegistro())
                .build();
    }
}