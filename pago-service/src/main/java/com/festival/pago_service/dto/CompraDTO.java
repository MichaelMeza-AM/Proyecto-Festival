package com.festival.pago_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CompraDTO {
    private Long id;
    private Long escenarioId;
    private int cantidad;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaAsistencia;
}