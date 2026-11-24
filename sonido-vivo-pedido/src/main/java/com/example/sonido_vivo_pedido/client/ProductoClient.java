package com.example.sonido_vivo_pedido.client;

import com.example.sonido_vivo_pedido.dto.ProductoApiResponse;
import com.example.sonido_vivo_pedido.dto.ProductoInfo;
import com.example.sonido_vivo_pedido.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductoClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.productos.url}")
    private String productosUrl;

    public ProductoInfo buscarPorId(Long productoId) {
        try {
            ProductoApiResponse respuesta = restTemplate.getForObject(
                    productosUrl + "/" + productoId,
                    ProductoApiResponse.class
            );

            if (respuesta == null || !respuesta.isSuccess()
                    || respuesta.getData() == null) {
                throw new RecursoNoEncontradoException(
                        "No se encontró el producto: " + productoId
                );
            }

            return respuesta.getData();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException(
                    "No se encontró el producto: " + productoId
            );
        }
    }
}