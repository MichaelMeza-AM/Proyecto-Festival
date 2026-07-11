package com.festival.ticket_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único generado por el sistema (Ej: TICK-89X)
    @Column(nullable = false, unique = true, length = 50)
    private String codigo; 

    @Column(nullable = false)
    private Long compraId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long escenarioId;

    @Column(nullable = false)
    private LocalDateTime fechaAsistencia;
}
