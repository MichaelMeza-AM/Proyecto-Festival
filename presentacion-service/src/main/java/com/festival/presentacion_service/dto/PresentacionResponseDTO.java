package com.festival.presentacion_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresentacionResponseDTO {

    private Long id;
    private Long artistaId;
    private Long escenarioId;
    private String nombreArtista;
    private String nombreEscenario;
    private Integer precioEscenario; 
    private String puertaAcceso; 
    private String nombreZona;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
}