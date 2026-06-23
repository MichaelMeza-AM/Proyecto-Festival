package com.festival.usuario_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;
    
    @Column(unique = true, nullable = false)
    private String email;

    // RUT único, pero permite nulos para extranjeros
    @Column(unique = true, length = 12) 
    private String rut;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
}