package com.example.sonido_vivo_contacto.controller;

import com.example.sonido_vivo_contacto.dto.*;
import com.example.sonido_vivo_contacto.service.ContactoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/contactos")
@Tag(name = "Contacto", description = "Endpoints para envío y gestión de consultas de clientes")
public class ContactoController {

    private final ContactoService contactoService;

    @Operation(summary = "Enviar mensaje de contacto (Público)")
    @PostMapping
    public ResponseEntity<ApiResponse<ContactoResponse>> enviar(
            @Valid @RequestBody ContactoRequest request) {
        ContactoResponse creado = contactoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ContactoResponse>builder()
                        .success(true)
                        .message("Mensaje enviado correctamente")
                        .data(creado)
                        .build()
        );
    }

    @Operation(summary = "Listar todos los mensajes (Solo ADMINISTRADOR)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<ContactoResponse>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.<List<ContactoResponse>>builder()
                        .success(true)
                        .message("Mensajes obtenidos correctamente")
                        .data(contactoService.listar())
                        .build()
        );
    }

    @Operation(summary = "Marcar mensaje como leído (Solo ADMINISTRADOR)")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/leido")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ContactoResponse>> marcarLeido(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<ContactoResponse>builder()
                        .success(true)
                        .message("Mensaje marcado como leído")
                        .data(contactoService.marcarComoLeido(id))
                        .build()
        );
    }

    @Operation(summary = "Eliminar mensaje de contacto (Solo ADMINISTRADOR)")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        contactoService.eliminar(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Mensaje eliminado correctamente")
                        .build()
        );
    }
}
