package com.example.sonido_vivo_auth.dto;

import com.example.sonido_vivo_auth.model.Rol;
import jakarta.validation.constraints.*;

public record CrearTrabajadorRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 12, message = "La contraseña debe tener entre 8 y 12 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {}