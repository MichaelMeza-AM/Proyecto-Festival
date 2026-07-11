package com.festival.pago_service.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TicketRequestDTO {
    private Long compraId;
    private Long usuarioId;
    private Long escenarioId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaAsistencia;
}