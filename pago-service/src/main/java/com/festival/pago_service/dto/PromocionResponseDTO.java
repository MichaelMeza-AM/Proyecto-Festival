package com.festival.pago_service.dto;

import lombok.Data;

@Data
public class PromocionResponseDTO {
    private Integer porcentajeDescuento;
    private boolean esValido;
    private String mensaje;
}