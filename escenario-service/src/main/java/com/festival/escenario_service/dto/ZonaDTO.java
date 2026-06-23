package com.festival.escenario_service.dto;

import com.festival.escenario_service.model.Zona;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZonaDTO {
    @NotNull(message = "El ID de la zona es obligatorio")
    private Long id;

    @NotBlank(message = "El nombre de la zona es obligatorio")
    private String nombre;
    
    private String descripcion;

    public Zona toModel() {
        return new Zona(id, nombre, descripcion);
    }

    public static ZonaDTO fromModel(Zona z) {
        if (z == null) return null;
        return new ZonaDTO(z.getId(), z.getNombre(), z.getDescripcion());
    }
}