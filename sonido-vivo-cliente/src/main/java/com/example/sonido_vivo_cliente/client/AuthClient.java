package com.example.sonido_vivo_cliente.client;

import com.example.sonido_vivo_cliente.dto.ApiResponse;
import com.example.sonido_vivo_cliente.dto.AuthRegistroRequest;
import com.example.sonido_vivo_cliente.dto.AuthRegistroResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(WebClient.Builder builder, @Value("${app.auth.url}") String authUrl) {
        this.webClient = builder.baseUrl(authUrl).build();
    }

    public AuthRegistroResponse registrarUsuario(String correo, String password) {
        ApiResponse<AuthRegistroResponse> response = webClient.post()
                .uri("/registro")
                .bodyValue(new AuthRegistroRequest(correo, password))
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new IllegalArgumentException("Error en Auth: " + body)))
                )
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<AuthRegistroResponse>>() {})
                .block();

        if (response == null || !response.isSuccess() || response.getData() == null) {
            throw new IllegalStateException("Respuesta inválida desde el servicio de autenticación.");
        }

        return response.getData();
    }
}