package com.festival.itinerario_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "itinerario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "presentacion_id", nullable = false)
    private Long presentacionId;

    @Column(name = "fecha_agregado", nullable = false)
    private LocalDateTime fechaAgregado;
}
