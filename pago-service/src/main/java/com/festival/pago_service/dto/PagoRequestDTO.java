package com.festival.pago_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoRequestDTO {

    @NotNull(message = "El ID de la compra es obligatorio")
    private Long idCompra;

    @NotBlank(message = "El medio de pago es obligatorio")
    private String medioPago;

    @NotNull(message = "El % de descuento es obligatorio")
    @Min(value = 0, message = "El descuento mínimo es 0%")
    @Max(value = 100, message = "El descuento máximo es 100%")
    private Integer porcentajeDescuento;
}