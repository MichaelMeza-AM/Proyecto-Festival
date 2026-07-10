package com.festival.promocion_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.festival.promocion_service.model.Promocion;

@Data
public class PromocionRequestDTO {
    @NotBlank(message = "El código no puede estar vacío")
    private String codigo;

    @NotNull(message = "El descuento es obligatorio")
    @Min(value = 1, message = "El descuento mínimo es 1%")
    @Max(value = 100, message = "El descuento máximo es 100%")
    private Integer porcentajeDescuento;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaFin;

    private Boolean activo;

    public Promocion toModel() {
       return new Promocion(null, codigo, porcentajeDescuento, fechaInicio, fechaFin, activo);
    }
}