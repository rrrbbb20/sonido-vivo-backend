package com.example.sonido_vivo_pedido.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoApiResponse {

    private boolean success;
    private String message;
    private ProductoInfo data;
    private Object error;
}