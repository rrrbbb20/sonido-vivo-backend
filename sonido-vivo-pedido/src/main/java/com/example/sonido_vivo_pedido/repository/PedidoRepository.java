package com.example.sonido_vivo_pedido.repository;

import com.example.sonido_vivo_pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}