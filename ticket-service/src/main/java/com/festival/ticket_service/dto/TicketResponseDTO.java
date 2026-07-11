package com.festival.ticket_service.dto;

import com.festival.ticket_service.model.Ticket;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TicketResponseDTO {
    private Long id;
    private String codigo; 
    private Long compraId;
    private Long usuarioId;
    private Long escenarioId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaAsistencia;

    public static TicketResponseDTO fromModel(Ticket ticket) {
        if (ticket == null) return null;
        
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(ticket.getId());
        dto.setCodigo(ticket.getCodigo());
        dto.setCompraId(ticket.getCompraId());
        dto.setUsuarioId(ticket.getUsuarioId());
        dto.setEscenarioId(ticket.getEscenarioId());
        dto.setFechaAsistencia(ticket.getFechaAsistencia());
        
        return dto;
    }
}