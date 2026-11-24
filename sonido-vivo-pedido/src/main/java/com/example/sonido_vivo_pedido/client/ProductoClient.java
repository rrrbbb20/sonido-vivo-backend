package com.example.sonido_vivo_pedido.client;

import com.example.sonido_vivo_pedido.dto.ProductoApiResponse;
import com.example.sonido_vivo_pedido.dto.ProductoInfo;
import com.example.sonido_vivo_pedido.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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

    public void descontarStock(
            Long productoId,
            Integer cantidad,
            String authorizationHeader) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

        HttpEntity<Void> solicitud = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    productosUrl + "/" + productoId
                            + "/descontar-stock?cantidad=" + cantidad,
                    HttpMethod.PUT,
                    solicitud,
                    String.class
            );
        } catch (HttpClientErrorException.Conflict ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente para el producto: " + productoId
            );
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad para descontar debe ser válida"
            );
        }
    }
}