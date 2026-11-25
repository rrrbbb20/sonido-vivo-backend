
package com.example.ms_producto.repository;

import com.example.ms_producto.model.Producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaIgnoreCase(String categoria);

    List<Producto> findByMarcaIgnoreCase(String marca);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Producto p
            SET p.stock = p.stock - :cantidad
            WHERE p.id = :id
            AND p.stock >= :cantidad
            """)
    int restarStock(@Param("id") Long id,
                    @Param("cantidad") Integer cantidad);
}
