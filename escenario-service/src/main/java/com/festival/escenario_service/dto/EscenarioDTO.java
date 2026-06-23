package com.festival.escenario_service.dto;

import com.festival.escenario_service.model.Escenario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
public class EscenarioDTO {
    
    private Long id;

    @NotBlank(message = "El nombre del escenario es obligatorio")
    private String nombre;

    @NotBlank(message = "La puerta de acceso logística es obligatoria")
    private String puertaAcceso;

    @NotNull(message = "El aforo máximo es obligatorio")
    @Min(value = 1, message = "El aforo debe ser mayor a cero")
    private Integer aforoMaximo;

    @NotNull(message = "El precio del escenario es obligatorio")
    @Min(value = 0, message = "El precio del escenario no puede ser negativo")
    private Integer precio;

    @NotNull(message = "La zona geográfica es obligatoria")
    @Valid // Asegura que si se envía el objeto zona, se validen sus restricciones internas (@NotBlank)
    private ZonaDTO zona; // Al validar el escenario, exigimos que este objeto no sea null

    public Escenario toModel() {
        return new Escenario(
            id, 
            nombre, 
            puertaAcceso, 
            aforoMaximo, 
            precio, 
            this.zona != null ? this.zona.toModel() : null
        );
    }

    public static EscenarioDTO fromModel(Escenario e) {
        if (e == null) return null;
        return new EscenarioDTO(
            e.getId(), 
            e.getNombre(), 
            e.getPuertaAcceso(), 
            e.getAforoMaximo(), 
            e.getPrecio(), 
            ZonaDTO.fromModel(e.getZona())
        );
    }
}