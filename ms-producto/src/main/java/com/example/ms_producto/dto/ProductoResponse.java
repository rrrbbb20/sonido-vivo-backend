
package com.example.ms_producto.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Long id;

    private String categoria;

    private String nombre;

    private String marca;

    private String modelo;

    private String descripcionBreve;

    private Integer precio;

    private Integer stock;

    private String imagen;
}
