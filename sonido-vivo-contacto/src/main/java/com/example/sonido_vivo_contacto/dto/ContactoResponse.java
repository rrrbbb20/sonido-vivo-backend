package com.example.sonido_vivo_contacto.dto;

import java.time.LocalDateTime;

public record ContactoResponse(
        Long id,
        String nombre,
        String correo,
        String mensaje,
        boolean leido,
        LocalDateTime fechaCreacion
) {}