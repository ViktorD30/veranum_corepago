package com.veranum.corepago.service;

import com.veranum.corepago.client.ReservaClient;
import com.veranum.corepago.dto.PagoRequest;
import com.veranum.corepago.dto.PagoResponse;
import com.veranum.corepago.dto.ReservaResponse;
import com.veranum.corepago.exception.BusinessException;
import com.veranum.corepago.exception.ResourceNotFoundException;
import com.veranum.corepago.model.Pago;
import com.veranum.corepago.repository.PagoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private static final Logger log =
            LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final ReservaClient reservaClient;

    public PagoService(
            PagoRepository pagoRepository,
            ReservaClient reservaClient) {

        this.pagoRepository = pagoRepository;
        this.reservaClient = reservaClient;
    }

    public PagoResponse crearPago(PagoRequest request) {

        log.info(
                "Iniciando pago para la reserva ID: {}",
                request.getReservaId()
        );

        // 1. Evitar pagos duplicados
        if (pagoRepository.existsByReservaId(
                request.getReservaId())) {

            throw new BusinessException(
                    "La reserva ya posee un pago registrado"
            );
        }

        // 2. Consultar Reserva mediante RestClient
        ReservaResponse reserva =
                reservaClient.obtenerReserva(
                        request.getReservaId()
                );

        // 3. No permitir pagar reservas canceladas
        if ("CANCELADA".equalsIgnoreCase(
                reserva.getEstado())) {

            throw new BusinessException(
                    "No se puede registrar un pago para una reserva cancelada"
            );
        }

        // 4. Validar que la reserva tenga costo
        if (reserva.getCostoTotal() == null) {

            throw new BusinessException(
                    "La reserva no posee un costo total válido"
            );
        }

        // 5. Comprobar monto
        if (Double.compare(
                request.getMonto(),
                reserva.getCostoTotal()
        ) != 0) {

            throw new BusinessException(
                    "El monto ingresado no coincide con el costo total de la reserva. "
                            + "Monto esperado: "
                            + reserva.getCostoTotal()
            );
        }

        // 6. Crear pago
        Pago pago = new Pago();

        pago.setReservaId(
                request.getReservaId()
        );

        pago.setMonto(
                request.getMonto()
        );

        pago.setMetodoPago(
                request.getMetodoPago().toUpperCase()
        );

        pago.setEstado("PAGADO");

        pago.setFechaPago(
                LocalDateTime.now()
        );

        Pago guardado =
                pagoRepository.save(pago);

        log.info(
                "Pago registrado correctamente con ID: {}",
                guardado.getId()
        );

        return convertirResponse(guardado);
    }

    public List<PagoResponse> obtenerTodos() {

        return pagoRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    public PagoResponse obtenerPorId(Long id) {

        return convertirResponse(
                buscarEntidad(id)
        );
    }

    public List<PagoResponse> obtenerPorReserva(
            Long reservaId) {

        return pagoRepository
                .findAllByReservaId(reservaId)
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    public PagoResponse actualizarEstado(
            Long id,
            String nuevoEstado) {

        Pago pago =
                buscarEntidad(id);

        String estado =
                nuevoEstado.toUpperCase();

        if (!estado.equals("PENDIENTE")
                && !estado.equals("PAGADO")
                && !estado.equals("RECHAZADO")) {

            throw new BusinessException(
                    "El estado debe ser PENDIENTE, PAGADO o RECHAZADO"
            );
        }

        pago.setEstado(estado);

        Pago actualizado =
                pagoRepository.save(pago);

        log.info(
                "Estado del pago {} actualizado a {}",
                id,
                estado
        );

        return convertirResponse(actualizado);
    }

    public void eliminarPago(Long id) {

        Pago pago =
                buscarEntidad(id);

        pagoRepository.delete(pago);

        log.info(
                "Pago {} eliminado correctamente",
                id
        );
    }

    private Pago buscarEntidad(Long id) {

        return pagoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pago no encontrado con ID: " + id
                        )
                );
    }

    private PagoResponse convertirResponse(
            Pago pago) {

        PagoResponse response =
                new PagoResponse();

        response.setId(
                pago.getId()
        );

        response.setReservaId(
                pago.getReservaId()
        );

        response.setMonto(
                pago.getMonto()
        );

        response.setMetodoPago(
                pago.getMetodoPago()
        );

        response.setEstado(
                pago.getEstado()
        );

        response.setFechaPago(
                pago.getFechaPago()
        );

        return response;
    }
}