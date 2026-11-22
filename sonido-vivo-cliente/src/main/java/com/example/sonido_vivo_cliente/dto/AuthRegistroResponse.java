package com.example.sonido_vivo_cliente.dto;

public record AuthRegistroResponse(
        Long idUsuario,
        String token,
        String correo,
        String rol
) {}