package com.example.sonido_vivo_auth.controller;

import com.example.sonido_vivo_auth.dto.*;
import com.example.sonido_vivo_auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para login, registro de clientes y gestión de trabajadores")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y emite un token JWT.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Inicio de sesión exitoso")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Registrar nuevo cliente", description = "Crea una cuenta pública asignando automáticamente el rol CLIENTE.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente registrado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Correo duplicado o datos inválidos")
    })
    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<LoginResponse>> registrar(@Valid @RequestBody RegistroRequest request) {
        LoginResponse response = authService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Usuario registrado correctamente")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Crear cuenta de trabajador", description = "Permite a un ADMINISTRADOR crear cuentas con rol VENDEDOR o ADMINISTRADOR.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Trabajador creado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o correo existente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRADOR")
    })
    @PostMapping("/trabajadores")
    public ResponseEntity<ApiResponse<LoginResponse>> crearTrabajador(@Valid @RequestBody CrearTrabajadorRequest request) {
        LoginResponse response = authService.crearTrabajador(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Trabajador registrado correctamente")
                        .data(response)
                        .build()
        );
    }
}