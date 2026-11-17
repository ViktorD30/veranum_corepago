package com.veranum.corepago.controller;

import com.veranum.corepago.dto.ApiResponse;
import com.veranum.corepago.dto.EstadoPagoRequest;
import com.veranum.corepago.dto.PagoRequest;
import com.veranum.corepago.dto.PagoResponse;
import com.veranum.corepago.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/v1/pagos")
@Tag(
        name = "Gestión de Pagos",
        description = "Operaciones para registrar, consultar, actualizar y eliminar pagos"
)
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }


    @Operation(
        summary = "Registrar un pago",
        description = "Registra un pago validando previamente la reserva y su costo total"
)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Pago registrado correctamente"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Datos inválidos, monto incorrecto o reserva ya pagada"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Reserva no encontrada"
        )
})

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponse>> crearPago(
            @Valid @RequestBody PagoRequest request) {

        PagoResponse pago =
                pagoService.crearPago(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                201,
                                "Pago registrado correctamente",
                                false,
                                pago
                        )
                );
    }


    @Operation(
        summary = "Listar todos los pagos",
        description = "Obtiene todos los pagos registrados en el sistema"
)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Pagos obtenidos correctamente"
        )
})
    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoResponse>>> obtenerTodos() {

        List<PagoResponse> pagos =
                pagoService.obtenerTodos();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Pagos obtenidos correctamente",
                        false,
                        pagos
                )
        );
    }


    @Operation(
        summary = "Buscar pago por ID",
        description = "Obtiene un pago específico utilizando su identificador"
)
    @ApiResponses({

        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Pago encontrado"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Pago no encontrado"
        )
})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoResponse>> obtenerPorId(
            @PathVariable Long id) {

        PagoResponse pago =
                pagoService.obtenerPorId(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Pago encontrado",
                        false,
                        pago
                )
        );
    }

    @Operation(
        summary = "Buscar pagos por reserva",
        description = "Obtiene los pagos asociados al identificador de una reserva"
)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Pagos de la reserva obtenidos correctamente"
        )
})

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> obtenerPorReserva(
            @PathVariable Long reservaId) {

        List<PagoResponse> pagos =
                pagoService.obtenerPorReserva(reservaId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Pagos de la reserva obtenidos correctamente",
                        false,
                        pagos
                )
        );
    }
    @Operation(
        summary = "Actualizar estado del pago",
        description = "Permite cambiar el estado de un pago a PENDIENTE, PAGADO o RECHAZADO"
)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Estado actualizado correctamente"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Estado inválido"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Pago no encontrado"
        )
})

    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<PagoResponse>> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoPagoRequest request) {

        PagoResponse pago =
                pagoService.actualizarEstado(
                        id,
                        request.getEstado()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Estado del pago actualizado correctamente",
                        false,
                        pago
                )
        );
    }

    @Operation(
        summary = "Eliminar un pago",
        description = "Elimina un pago utilizando su identificador"
)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Pago eliminado correctamente"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Pago no encontrado"
        )
})

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminarPago(
            @PathVariable Long id) {

        pagoService.eliminarPago(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Pago eliminado correctamente",
                        false,
                        null
                )
        );
    }
}