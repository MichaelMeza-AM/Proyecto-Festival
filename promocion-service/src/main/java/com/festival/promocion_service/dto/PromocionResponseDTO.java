package com.festival.promocion_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.festival.promocion_service.model.Promocion;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromocionResponseDTO {
    private Long id;
    private String codigo;
    private Integer porcentajeDescuento;
    private Boolean activo;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaFin;
    
    private boolean esValido; 
    private String mensaje;   

    public static PromocionResponseDTO fromModel(Promocion p) {
        if (p == null) return null;
        
        PromocionResponseDTO dto = new PromocionResponseDTO();
        dto.setId(p.getId());
        dto.setCodigo(p.getCodigo());
        dto.setPorcentajeDescuento(p.getPorcentajeDescuento());
        dto.setActivo(p.getActivo());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaFin(p.getFechaFin());
        
        return dto;
    }
}