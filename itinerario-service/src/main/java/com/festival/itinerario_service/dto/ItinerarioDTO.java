package com.festival.itinerario_service.dto;

import com.festival.itinerario_service.model.Itinerario;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItinerarioDTO {
    
    private Long id;
    private Long usuarioId;
    
    @NotNull(message = "El ID de la presentación es obligatorio")
    private Long presentacionId;
    
    private LocalDateTime fechaAgregado;

    public Itinerario toModel() {
        return new Itinerario(id, usuarioId, presentacionId, fechaAgregado);
    }

    public static ItinerarioDTO fromModel(Itinerario i) {
        if (i == null) return null;
        return new ItinerarioDTO(i.getId(), i.getUsuarioId(), i.getPresentacionId(), i.getFechaAgregado());
    }
}