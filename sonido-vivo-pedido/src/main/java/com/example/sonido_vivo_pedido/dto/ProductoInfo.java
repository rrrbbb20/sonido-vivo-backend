package com.example.sonido_vivo_pedido.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoInfo {

    private Long id;
    private String nombre;
    private Integer precio;
    private Integer stock;
}