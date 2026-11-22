package com.example.sonido_vivo_cliente.dto;

public record AuthRegistroResponse(
        Long id,
        String token,
        String correo,
        String rol
) {}