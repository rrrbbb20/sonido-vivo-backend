package com.example.sonido_vivo_auth.dto;

public record LoginResponse(
        Long idUsuario,
        String token,
        String correo,
        String rol
) {}