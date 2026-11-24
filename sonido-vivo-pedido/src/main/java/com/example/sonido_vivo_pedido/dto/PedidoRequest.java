package com.example.sonido_vivo_pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser mayor que cero")
    private Long clienteId;

    @NotBlank(message = "El tipo de entrega es obligatorio")
    private String tipoEntrega;

    private String direccionEntrega;

    @NotEmpty(message = "El pedido debe incluir al menos un producto")
    @Valid
    private List<DetallePedidoRequest> detalles;
}