package com.example.sonido_vivo_auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}