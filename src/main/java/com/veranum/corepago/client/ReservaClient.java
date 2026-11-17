package com.veranum.corepago.client;

import com.veranum.corepago.dto.ApiResponse;
import com.veranum.corepago.dto.ReservaResponse;
import com.veranum.corepago.exception.BusinessException;
import com.veranum.corepago.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ReservaClient {

    private final RestClient restClient;

    public ReservaClient(
            RestClient.Builder builder,
            @Value("${reserva.service.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public ReservaResponse obtenerReserva(Long id) {

        try {

            ApiResponse<ReservaResponse> respuesta =
                    restClient.get()
                            .uri("/api/v1/reservas/{id}", id)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            ApiResponse<ReservaResponse>>() {
                                    }
                            );

            if (respuesta == null || respuesta.getData() == null) {
                throw new ResourceNotFoundException(
                        "La reserva solicitada no existe"
                );
            }

            return respuesta.getData();

        } catch (ResourceNotFoundException ex) {

            throw ex;

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException(
                        "Reserva no encontrada con ID: " + id
                );
            }

            throw new BusinessException(
                    "Error al consultar el microservicio de reservas"
            );

        } catch (Exception ex) {

            throw new BusinessException(
                    "No fue posible comunicarse con el microservicio de reservas"
            );
        }
    }
}