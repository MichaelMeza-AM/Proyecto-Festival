package com.festival.pago_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long idCompra;
    
    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Integer montoSubtotal;

    @Column(nullable = false)
    private Integer porcentajeDescuento;

    @Column(nullable = false)
    private Integer montoDescuento;

    @Column(nullable = false)
    private Integer iva;

    @Column(nullable = false)
    private Integer montoTotal;

    @Column(nullable = false, length = 50)
    private String medioPago;

    @Column(nullable = false)
    private LocalDateTime fechaPago;
}