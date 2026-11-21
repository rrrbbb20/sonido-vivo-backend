
package com.example.ms_producto.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "producto")
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String categoria;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(name = "descripcion_breve", length = 500)
    private String descripcionBreve;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer stock;


    private String imagen;
}
