package com.example.ms_producto.controller;

import com.example.ms_producto.dto.ApiResponse;
import com.example.ms_producto.dto.ProductoRequest;
import com.example.ms_producto.dto.ProductoResponse;
import com.example.ms_producto.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productos")
@Tag(
        name = "Productos",
        description = "Endpoints para la gestión de productos de Sonido Vivo"
)
public class ProductoController {

    private final ProductoService productoService;

    // =====================================================
    // 1. LISTAR TODOS LOS PRODUCTOS
    // =====================================================

    @Operation(
            summary = "Listar todos los productos",
            description = "Retorna todos los productos registrados en el catálogo."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Productos obtenidos correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listar() {

        List<ProductoResponse> productos = productoService.listar();

        return ResponseEntity.ok(
                ApiResponse.<List<ProductoResponse>>builder()
                        .success(true)
                        .message("Productos obtenidos correctamente")
                        .data(productos)
                        .build()
        );
    }

    // =====================================================
    // 2. BUSCAR PRODUCTO POR ID
    // =====================================================

    @Operation(
            summary = "Buscar producto por ID",
            description = "Retorna un producto mediante su identificador."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> buscarPorId(
            @Parameter(
                    description = "ID del producto que se desea buscar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        ProductoResponse producto = productoService.buscarPorId(id);

        return ResponseEntity.ok(
                ApiResponse.<ProductoResponse>builder()
                        .success(true)
                        .message("Producto encontrado correctamente")
                        .data(producto)
                        .build()
        );
    }

    // =====================================================
    // 3. BUSCAR PRODUCTOS POR CATEGORIA
    // =====================================================

    @Operation(
            summary = "Buscar productos por categoría",
            description = "Retorna los productos que pertenecen a una categoría determinada."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda por categoría realizada correctamente"
            )
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> buscarPorCategoria(
            @Parameter(
                    description = "Categoría de los productos",
                    example = "Guitarras",
                    required = true
            )
            @PathVariable String categoria) {

        List<ProductoResponse> productos =
                productoService.buscarPorCategoria(categoria);

        return ResponseEntity.ok(
                ApiResponse.<List<ProductoResponse>>builder()
                        .success(true)
                        .message("Productos filtrados por categoría")
                        .data(productos)
                        .build()
        );
    }

    // =====================================================
    // 4. BUSCAR PRODUCTOS POR MARCA
    // =====================================================

    @Operation(
            summary = "Buscar productos por marca",
            description = "Retorna los productos registrados de una marca determinada."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda por marca realizada correctamente"
            )
    })
    @GetMapping("/marca/{marca}")
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> buscarPorMarca(
            @Parameter(
                    description = "Marca de los productos",
                    example = "Epiphone",
                    required = true
            )
            @PathVariable String marca) {

        List<ProductoResponse> productos =
                productoService.buscarPorMarca(marca);

        return ResponseEntity.ok(
                ApiResponse.<List<ProductoResponse>>builder()
                        .success(true)
                        .message("Productos filtrados por marca")
                        .data(productos)
                        .build()
        );
    }

    // =====================================================
    // 5. BUSCAR PRODUCTOS POR NOMBRE
    // =====================================================

    @Operation(
            summary = "Buscar productos por nombre",
            description = "Retorna los productos cuyo nombre contiene el texto proporcionado."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda por nombre realizada correctamente"
            )
    })
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> buscarPorNombre(
            @Parameter(
                    description = "Nombre o parte del nombre del producto",
                    example = "Guitarra",
                    required = true
            )
            @RequestParam String nombre) {

        List<ProductoResponse> productos =
                productoService.buscarPorNombre(nombre);

        return ResponseEntity.ok(
                ApiResponse.<List<ProductoResponse>>builder()
                        .success(true)
                        .message("Búsqueda realizada correctamente")
                        .data(productos)
                        .build()
        );
    }

    // =====================================================
    // 6. REGISTRAR PRODUCTO
    // VENDEDOR O ADMINISTRADOR
    // =====================================================

    @Operation(
            summary = "Registrar un producto",
            description = "Permite registrar un producto nuevo. Requiere rol VENDEDOR o ADMINISTRADOR."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Producto registrado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos del producto inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "No tienes permisos suficientes"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> registrar(
            @Valid @RequestBody ProductoRequest request) {

        ProductoResponse producto = productoService.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProductoResponse>builder()
                        .success(true)
                        .message("Producto registrado correctamente")
                        .data(producto)
                        .build()
        );
    }

    // =====================================================
    // 7. ACTUALIZAR PRODUCTO
    // VENDEDOR O ADMINISTRADOR
    // =====================================================

    @Operation(
            summary = "Actualizar un producto",
            description = "Permite actualizar un producto existente. Requiere rol VENDEDOR o ADMINISTRADOR."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos del producto inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "No tienes permisos suficientes"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(
            @Parameter(
                    description = "ID del producto que se desea actualizar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {

        ProductoResponse producto = productoService.actualizar(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ProductoResponse>builder()
                        .success(true)
                        .message("Producto actualizado correctamente")
                        .data(producto)
                        .build()
        );
    }

    // =====================================================
    // 8. ELIMINAR PRODUCTO
    // SOLO ADMINISTRADOR
    // =====================================================

    @Operation(
            summary = "Eliminar un producto",
            description = "Permite eliminar un producto mediante su ID. Requiere rol ADMINISTRADOR."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Producto eliminado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "No tienes permisos suficientes"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(
            @Parameter(
                    description = "ID del producto que se desea eliminar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        productoService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("Producto eliminado correctamente")
                        .build()
        );
    }

    // =====================================================
    // 9. DESCONTAR STOCK AL CONFIRMAR COMPRA
    // USUARIO AUTENTICADO
    // =====================================================

    @Operation(
            summary = "Descontar stock de un producto",
            description = "Resta una cantidad del stock disponible de un producto al confirmar un pedido."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Stock descontado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Cantidad inválida"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Producto inexistente o stock insuficiente"
            )
    })
    @PutMapping("/{id}/descontar-stock")
    public ResponseEntity<ApiResponse<Object>> restarStock(
            @Parameter(
                    description = "ID del producto",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Parameter(
                    description = "Cantidad de unidades a descontar",
                    example = "2",
                    required = true
            )
            @RequestParam Integer cantidad) {

        productoService.restarStock(id, cantidad);

        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("Stock descontado correctamente")
                        .build()
        );
    }
}