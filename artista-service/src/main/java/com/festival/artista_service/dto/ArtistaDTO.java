package com.festival.artista_service.dto;

import com.festival.artista_service.model.Artista;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistaDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre del artista es obligatorio")
    private String nombre;
    
    @Size(max = 600, message = "La biografía no puede exceder los 600 caracteres")
    private String biografia;
    
    @NotBlank(message = "El género musical es obligatorio")
    private String generoMusical;

    public Artista toModel() {
        return new Artista(id, nombre, biografia, generoMusical);
    }

    public static ArtistaDTO fromModel(Artista a) {
        if (a == null) return null;
        return new ArtistaDTO(a.getId(), a.getNombre(), a.getBiografia(), a.getGeneroMusical());
    }
}