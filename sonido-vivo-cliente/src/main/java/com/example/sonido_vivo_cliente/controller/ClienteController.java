package com.example.sonido_vivo_cliente.controller;

import com.example.sonido_vivo_cliente.dto.*;
import com.example.sonido_vivo_cliente.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clientes")
@Tag(
        name = "Clientes",
        description = "Endpoints para el registro y gestión de clientes de Sonido Vivo"
)
public class ClienteController {

    private final ClienteService clienteService;

    // =====================================================
    // 1. REGISTRAR NUEVO CLIENTE
    // ACCESO PÚBLICO
    // =====================================================

    @Operation(
            summary = "Registrar nuevo cliente",
            description = "Crea la cuenta en ms-auth y registra el perfil del cliente."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Cliente registrado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos del cliente inválidos o correo duplicado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "El RUT ya se encuentra registrado"
            )
    })
    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<ClienteRegistroResponse>> registrar(
            @Valid @RequestBody ClienteRegistroRequest request) {

        ClienteRegistroResponse respuesta = clienteService.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ClienteRegistroResponse>builder()
                        .success(true)
                        .message("Cliente registrado correctamente")
                        .data(respuesta)
                        .build()
        );
    }

    // =====================================================
    // 2. LISTAR TODOS LOS CLIENTES
    // SOLO ADMINISTRADOR
    // =====================================================

    @Operation(
            summary = "Listar todos los clientes",
            description = "Retorna todos los clientes registrados. Requiere rol ADMINISTRADOR."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Clientes obtenidos correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "No tienes permisos suficientes"
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> listar() {

        List<ClienteResponse> clientes = clienteService.listar();

        return ResponseEntity.ok(
                ApiResponse.<List<ClienteResponse>>builder()
                        .success(true)
                        .message("Clientes obtenidos correctamente")
                        .data(clientes)
                        .build()
        );
    }

    // =====================================================
    // 3. BUSCAR CLIENTE POR ID
    // USUARIO AUTENTICADO
    // =====================================================

    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna el perfil de un cliente mediante su identificador."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cliente encontrado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> buscarPorId(
            @Parameter(
                    description = "ID del cliente que se desea buscar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        ClienteResponse cliente = clienteService.buscarPorId(id);

        return ResponseEntity.ok(
                ApiResponse.<ClienteResponse>builder()
                        .success(true)
                        .message("Cliente encontrado correctamente")
                        .data(cliente)
                        .build()
        );
    }

    // =====================================================
    // 4. BUSCAR CLIENTE POR USUARIO ID (AUTH)
    // USUARIO AUTENTICADO
    // =====================================================

    @Operation(
            summary = "Buscar cliente por ID de usuario",
            description = "Retorna el perfil asociado al ID de usuario emitido por ms-auth."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Perfil de cliente encontrado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Perfil de cliente no encontrado"
            )
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<ClienteResponse>> buscarPorUsuarioId(
            @Parameter(
                    description = "ID del usuario en ms-auth",
                    example = "1",
                    required = true
            )
            @PathVariable Long usuarioId) {

        ClienteResponse cliente = clienteService.buscarPorUsuarioId(usuarioId);

        return ResponseEntity.ok(
                ApiResponse.<ClienteResponse>builder()
                        .success(true)
                        .message("Perfil de cliente encontrado correctamente")
                        .data(cliente)
                        .build()
        );
    }

    // =====================================================
    // 5. ACTUALIZAR PERFIL DE CLIENTE
    // USUARIO AUTENTICADO
    // =====================================================

    @Operation(
            summary = "Actualizar datos del cliente",
            description = "Permite actualizar nombre, apellido y teléfono de un cliente existente."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cliente actualizado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos del cliente inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizar(
            @Parameter(
                    description = "ID del cliente que se desea actualizar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteRequest request) {

        ClienteResponse actualizado = clienteService.actualizar(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ClienteResponse>builder()
                        .success(true)
                        .message("Cliente actualizado correctamente")
                        .data(actualizado)
                        .build()
        );
    }
}