package com.festival.presentacion_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Entity
@Table(name = "presentacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Presentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artista_id", nullable = false)
    private Long artistaId;

    @Column(name = "escenario_id", nullable = false)
    private Long escenarioId;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;
}