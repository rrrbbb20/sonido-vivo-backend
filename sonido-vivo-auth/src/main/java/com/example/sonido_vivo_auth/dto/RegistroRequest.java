package com.example.sonido_vivo_auth.dto;

import jakarta.validation.constraints.*;

public record RegistroRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 12, message = "La contraseña debe tener entre 8 y 12 caracteres")
        String password
) {}