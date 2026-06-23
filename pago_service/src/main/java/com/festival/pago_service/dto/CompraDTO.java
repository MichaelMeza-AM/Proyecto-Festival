package com.festival.pago_service.dto;
import lombok.Data;
@Data

public class CompraDTO {
    private Long id;
    private Long escenarioId;
    private int cantidad;
}
