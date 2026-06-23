package com.festival.presentacion_service.dto;

import com.festival.presentacion_service.model.Presentacion;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat; 

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresentacionRequestDTO {
    
    @NotNull(message = "El ID del artista es obligatorio")
    private Long artistaId;
    
    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;
  
    @NotNull(message = "La fecha y hora de la presentación son obligatorias")
    @FutureOrPresent(message = "La presentación no puede ser programada en el pasado")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime fechaHora;
    
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 15, message = "La presentación debe durar al menos 15 minutos")
    private Integer duracionMinutos;

    // Método para convertir el DTO a Modelo limpiamente
    public Presentacion toModel() {
        Presentacion p = new Presentacion();
        p.setArtistaId(this.artistaId);
        p.setEscenarioId(this.escenarioId);
        p.setFechaHora(this.fechaHora);
        p.setDuracionMinutos(this.duracionMinutos);
        return p;
    }
}