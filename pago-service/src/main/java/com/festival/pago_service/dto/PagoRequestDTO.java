package com.festival.pago_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoRequestDTO {

    @NotNull(message = "El ID de la compra es obligatorio")
    private Long idCompra;

    @NotBlank(message = "El medio de pago es obligatorio")
    private String medioPago;

    private String codigoPromocion;
}