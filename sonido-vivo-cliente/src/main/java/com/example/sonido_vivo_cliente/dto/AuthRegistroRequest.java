package com.example.sonido_vivo_cliente.dto;

public record AuthRegistroRequest(
        String correo,
        String password
) {}