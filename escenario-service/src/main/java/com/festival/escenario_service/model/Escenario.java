package com.festival.escenario_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "escenario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Escenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    @Column(name = "puerta_acceso")
    private String puertaAcceso;
    
    @Column(name = "aforo_maximo")
    private Integer aforoMaximo;
    
    private Integer precio;

    // Relación: Muchos Escenarios pertenecen a 1 Zona del parque
    @ManyToOne
    @JoinColumn(name = "zona_id", nullable = false) // Columna FK obligatoria
    private Zona zona;
}

