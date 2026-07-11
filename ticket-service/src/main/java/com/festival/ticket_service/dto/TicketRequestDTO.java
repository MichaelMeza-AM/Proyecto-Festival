package com.festival.ticket_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TicketRequestDTO {

    @NotNull(message = "El ID de la compra es obligatorio")
    private Long compraId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;

    @NotNull(message = "La fecha de asistencia es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaAsistencia;
}