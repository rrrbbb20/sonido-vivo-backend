package com.example.sonido_vivo_pedido.service;

import com.example.sonido_vivo_pedido.exception.RecursoNoEncontradoException;
import com.example.sonido_vivo_pedido.model.Pedido;
import com.example.sonido_vivo_pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public List<Pedido> listar() {
        return repository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Pedido no encontrado: " + id
                        )
                );
    }
}