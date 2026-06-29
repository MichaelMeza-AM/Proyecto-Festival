package com.festival.compra_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.festival.compra_service.model.Compra;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long escenarioId;
    private int cantidad;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime fechaAsistencia;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaCompra;

    public static CompraResponseDTO fromModel(Compra compra) {
        if (compra == null) return null;
        return new CompraResponseDTO(
                compra.getId(),
                compra.getUsuarioId(),
                compra.getEscenarioId(),
                compra.getCantidad(),
                compra.getFechaAsistencia(),
                compra.getFechaCompra()
        );
    }
}