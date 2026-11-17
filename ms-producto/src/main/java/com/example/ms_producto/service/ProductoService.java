
package com.example.ms_producto.service;

import com.example.ms_producto.dto.ProductoRequest;
import com.example.ms_producto.dto.ProductoResponse;
import com.example.ms_producto.exception.RecursoNoEncontradoException;
import com.example.ms_producto.model.Producto;
import com.example.ms_producto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Listar todos los productos
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        log.info("Listando todos los productos");

        return productoRepository.findAll()
                .stream()
                .map(producto -> mapToResponse(producto))
                .toList();
    }

    // Buscar producto por ID
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        log.info("Buscando producto con ID: {}", id);

        Producto producto = buscarEntidadPorId(id);

        log.info("Producto encontrado. ID: {}", producto.getId());

        return mapToResponse(producto);
    }

    // Buscar productos por categoría
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorCategoria(String categoria) {
        log.info("Buscando productos por categoría: {}", categoria);

        return productoRepository.findByCategoriaIgnoreCase(categoria)
                .stream()
                .map(producto -> mapToResponse(producto))
                .toList();
    }

    // Buscar productos por marca
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorMarca(String marca) {
        log.info("Buscando productos por marca: {}", marca);

        return productoRepository.findByMarcaIgnoreCase(marca)
                .stream()
                .map(producto -> mapToResponse(producto))
                .toList();
    }

    // Buscar productos por nombre
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        log.info("Buscando productos por nombre: {}", nombre);

        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(producto -> mapToResponse(producto))
                .toList();
    }

    // Registrar un producto
    @Transactional
    public ProductoResponse registrar(ProductoRequest request) {
        log.info("Registrando producto: {}", request.getNombre());

        Producto producto = new Producto();

        copiarDatos(producto, request);

        Producto guardado = productoRepository.save(producto);

        log.info("Producto registrado correctamente. ID: {}", guardado.getId());

        return mapToResponse(guardado);
    }

    // Actualizar un producto
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        log.info("Actualizando producto con ID: {}", id);

        Producto producto = buscarEntidadPorId(id);

        copiarDatos(producto, request);

        Producto actualizado = productoRepository.save(producto);

        log.info("Producto actualizado correctamente. ID: {}", actualizado.getId());

        return mapToResponse(actualizado);
    }

    // Eliminar un producto
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando producto con ID: {}", id);

        Producto producto = buscarEntidadPorId(id);

        productoRepository.delete(producto);

        log.info("Producto eliminado correctamente. ID: {}", id);
    }

    // Buscar la entidad para operaciones internas
    private Producto buscarEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el producto con ID: " + id
                ));
    }
    // Descontar stock al confirmar una compra
    @Transactional
    public void restarStock(Long id, Integer cantidad) {

        log.info("Descontando {} unidades del producto con ID: {}",
                cantidad, id);

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        int filasAfectadas = productoRepository.restarStock(id, cantidad);

        if (filasAfectadas == 0) {
            log.warn("No se pudo descontar stock del producto con ID: {}", id);

            throw new IllegalStateException(
                    "No se pudo descontar el stock. Producto inexistente o stock insuficiente"
            );
        }

        log.info("Stock descontado correctamente. Producto ID: {}, cantidad: {}",
                id, cantidad);
    }

    // Copiar los datos del Request a la entidad
    private void copiarDatos(Producto producto, ProductoRequest request) {
        producto.setCategoria(request.getCategoria());
        producto.setNombre(request.getNombre());
        producto.setMarca(request.getMarca());
        producto.setModelo(request.getModelo());
        producto.setDescripcionBreve(request.getDescripcionBreve());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setImagen(request.getImagen());
    }

    // Convertir la entidad en un DTO de respuesta
    private ProductoResponse mapToResponse(Producto producto) {
        log.debug("Mapeando producto con ID: {}", producto.getId());

        return ProductoResponse.builder()
                .id(producto.getId())
                .categoria(producto.getCategoria())
                .nombre(producto.getNombre())
                .marca(producto.getMarca())
                .modelo(producto.getModelo())
                .descripcionBreve(producto.getDescripcionBreve())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .imagen(producto.getImagen())
                .build();
    }


}
