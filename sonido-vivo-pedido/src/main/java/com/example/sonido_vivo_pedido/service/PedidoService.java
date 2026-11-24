package com.example.sonido_vivo_pedido.service;

import com.example.sonido_vivo_pedido.client.ProductoClient;
import com.example.sonido_vivo_pedido.dto.DetallePedidoRequest;
import com.example.sonido_vivo_pedido.dto.PedidoRequest;
import com.example.sonido_vivo_pedido.dto.ProductoInfo;
import com.example.sonido_vivo_pedido.exception.RecursoNoEncontradoException;
import com.example.sonido_vivo_pedido.model.DetallePedido;
import com.example.sonido_vivo_pedido.model.Pedido;
import com.example.sonido_vivo_pedido.repository.DetallePedidoRepository;
import com.example.sonido_vivo_pedido.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoClient productoClient;

    public PedidoService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoClient productoClient) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoClient = productoClient;
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Pedido no encontrado: " + id
                        )
                );
    }

    @Transactional
    public Pedido crear(PedidoRequest request) {
        if ("DESPACHO".equalsIgnoreCase(request.getTipoEntrega())
                && (request.getDireccionEntrega() == null
                || request.getDireccionEntrega().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La direccion es obligatoria para pedidos con despacho"
            );
        }

        List<DetallePedido> detalles = new ArrayList<>();
        Set<Long> productosIncluidos = new HashSet<>();
        int total = 0;

        for (DetallePedidoRequest detalleRequest : request.getDetalles()) {
            Long productoId = detalleRequest.getProductoId();

            if (!productosIncluidos.add(productoId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No repitas el mismo producto en el pedido"
                );
            }

            ProductoInfo producto = productoClient.buscarPorId(productoId);

            if (producto.getStock() == null
                    || producto.getStock() < detalleRequest.getCantidad()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para el producto: " + productoId
                );
            }

            if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "El producto no tiene un precio valido"
                );
            }

            int subtotal = producto.getPrecio() * detalleRequest.getCantidad();
            total += subtotal;

            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(productoId);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());

            detalles.add(detalle);
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(request.getClienteId());
        pedido.setEstado("EN_PREPARACION");
        pedido.setTipoEntrega(request.getTipoEntrega());
        pedido.setDireccionEntrega(request.getDireccionEntrega());
        pedido.setTotal(total);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for (DetallePedido detalle : detalles) {
            detalle.setPedido(pedidoGuardado);
            detallePedidoRepository.save(detalle);
        }

        return pedidoGuardado;
    }
}