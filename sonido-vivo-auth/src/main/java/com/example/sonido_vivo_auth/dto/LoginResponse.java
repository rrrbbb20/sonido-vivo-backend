package com.example.sonido_vivo_auth.dto;

public record LoginResponse(
        String token,
        String correo,
        String rol
) {}