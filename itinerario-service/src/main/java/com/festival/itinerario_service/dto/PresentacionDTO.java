package com.festival.itinerario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor  
@AllArgsConstructor
public class PresentacionDTO {
    private Long id;
    private String nombreArtista;
    private String nombreEscenario;
    private String nombreZona;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaHora;
    
    private Integer duracionMinutos;
}