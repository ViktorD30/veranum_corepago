package com.veranum.corepago.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EstadoPagoRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(
            regexp = "^(PENDIENTE|PAGADO|RECHAZADO)$",
            message = "El estado debe ser PENDIENTE, PAGADO o RECHAZADO"
    )
    private String estado;

    public EstadoPagoRequest() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}