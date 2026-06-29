package com.festival.pago_service.dto;
import com.festival.pago_service.model.Pago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long idCompra;
    private Integer montoSubtotal;
    private Integer porcentajeDescuento;
    private Integer montoDescuento;
    private Integer iva;
    private Integer montoTotal;
    private String medioPago;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaPago;

    public static PagoResponseDTO fromModel(Pago pago) {
        if (pago == null) return null;
        return new PagoResponseDTO(
                pago.getId(),
                pago.getUsuarioId(),
                pago.getIdCompra(),
                pago.getMontoSubtotal(),
                pago.getPorcentajeDescuento(),
                pago.getMontoDescuento(),
                pago.getIva(),
                pago.getMontoTotal(),
                pago.getMedioPago(),
                pago.getFechaPago()
        );
    }
}