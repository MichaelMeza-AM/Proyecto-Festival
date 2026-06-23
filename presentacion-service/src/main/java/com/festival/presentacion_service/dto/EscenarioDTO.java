package com.festival.presentacion_service.dto;

import lombok.Data;

@Data
public class EscenarioDTO {
    private Long id;
    private String nombre;
    private String puertaAcceso;
    private Integer aforoMaximo;
    private Integer precio;
    private ZonaDTO zona; // Enlaza con el DTO espejo de Zona
}