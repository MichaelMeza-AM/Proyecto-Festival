package com.festival.auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioRegistroDTO {
    private Long id;
    private String email;
    private String nombre;
}