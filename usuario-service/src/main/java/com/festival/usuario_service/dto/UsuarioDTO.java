package com.festival.usuario_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.festival.usuario_service.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {

    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "El RUT debe ser válido y llevar guion (ej: 12345678-9)")
    private String rut;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate fechaNacimiento;

    public static UsuarioDTO fromModel(Usuario usuario) {
        if (usuario == null) return null;
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rut(usuario.getRut())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .build();
    }

    public Usuario toModel() {
        return new Usuario(id, nombre, email, rut, fechaNacimiento);
    }
}