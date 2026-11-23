package com.example.sonido_vivo_cliente.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private Long id;
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String rut;
    private String telefono;
    private LocalDateTime fechaRegistro;
}