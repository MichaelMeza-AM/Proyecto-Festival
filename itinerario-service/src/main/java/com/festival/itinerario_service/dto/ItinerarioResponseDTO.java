package com.festival.itinerario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor  
@AllArgsConstructor
public class ItinerarioResponseDTO {
    private Long id;
    private Long usuarioId;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaAgregado;
    
    // Aquí incrustamos la presentación enriquecida
    private PresentacionDTO presentacion; 
}