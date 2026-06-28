package com.festival.compra_service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CompraRequestDTO {

    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;

    @NotNull(message = "La fecha de asistencia es obligatoria")
    @FutureOrPresent(message = "La fecha de asistencia no puede ser en el pasado")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaAsistencia;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private int cantidad;
}