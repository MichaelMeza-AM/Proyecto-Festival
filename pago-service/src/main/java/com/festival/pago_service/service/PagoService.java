package com.festival.pago_service.service;

import com.festival.pago_service.dto.CompraDTO;
import com.festival.pago_service.dto.EscenarioDTO;
import com.festival.pago_service.dto.PagoRequestDTO;
import com.festival.pago_service.dto.PromocionResponseDTO;
import com.festival.pago_service.exception.BadRequestException;
import com.festival.pago_service.exception.ResourceNotFoundException;
import com.festival.pago_service.model.Pago;
import com.festival.pago_service.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import com.festival.pago_service.dto.TicketRequestDTO;

@Service
public class PagoService {

    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);
    private final PagoRepository pagoRepository;
    private final WebClient webClient;

    @Value("${api.compra.url}")
    private String compraUrl;

    @Value("${api.escenario.url}")
    private String escenarioUrl;

    @Value("${api.promocion.url}")
    private String promocionUrl;

    @Value("${api.ticket.url}")
    private String ticketUrl;

    public PagoService(PagoRepository pagoRepository, WebClient.Builder webClientBuilder) {
        this.pagoRepository = pagoRepository;
        this.webClient = webClientBuilder.build();
    }

    public Pago guardar(PagoRequestDTO request, String tokenAuth, Long usuarioId) {
        logger.info("Iniciando registro de pago para la compra ID={}", request.getIdCompra());

        validarQueNoEstePagada(request.getIdCompra());

        int porcentajeDescuento = obtenerDescuentoDelCupon(request.getCodigoPromocion(), tokenAuth);
        CompraDTO compra = obtenerDatosCompra(request.getIdCompra(), tokenAuth);
        EscenarioDTO escenario = obtenerDatosEscenario(compra.getEscenarioId(), tokenAuth);

        Pago nuevoPago = calcularYConstruirPago(usuarioId, request, compra, escenario, porcentajeDescuento);
        Pago pagoGuardado = pagoRepository.save(nuevoPago);

        // --- COMUNICACIÓN AUTOMÁTICA CON TICKETS ---
        logger.info("Pago exitoso. Generando {} tickets para la compra ID={}", compra.getCantidad(), request.getIdCompra());

        for (int i = 0; i < compra.getCantidad(); i++) {
            TicketRequestDTO ticketData = new TicketRequestDTO();
            ticketData.setCompraId(request.getIdCompra());
            ticketData.setUsuarioId(usuarioId);
            ticketData.setEscenarioId(compra.getEscenarioId());
            
            // transformamos el LocalDate agregándole las 00:00:00
            ticketData.setFechaAsistencia(compra.getFechaAsistencia().atStartOfDay());

            try {
                webClient.post()
                        .uri(ticketUrl)
                        .header("Authorization", tokenAuth)
                        .bodyValue(ticketData)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                        
                logger.info("Ticket {} generado exitosamente", (i + 1));
            } catch (Exception e) {
                logger.error("Error al generar ticket: {}", e.getMessage());
            }
        }

        return pagoGuardado;
    }

    public List<Pago> buscarTodos() {
        logger.info("Buscando todos los registros de pago históricos");
        return pagoRepository.findAll();
    }

    public Pago buscarPorId(Long id) {
        logger.info("Buscando registro de pago por ID={}", id);
        return pagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se encontró el registro de pago con ID={}", id);
                    return new ResourceNotFoundException("El registro de pago solicitado no existe.");
                });
    }

    public Pago actualizar(Long id, PagoRequestDTO request) {
        logger.info("Iniciando proceso de actualización para el pago ID={}", id);
        Pago existente = buscarPorId(id);
        existente.setMedioPago(request.getMedioPago());
        return pagoRepository.save(existente);
    }

    public void eliminar(Long id) {
        logger.info("Iniciando proceso de eliminación para el pago ID={}", id);
        if (!pagoRepository.existsById(id)) {
            logger.warn("Cancelando eliminación: El registro de pago ID={} no existe.", id);
            throw new ResourceNotFoundException("No se puede eliminar: el pago no existe.");
        }
        pagoRepository.deleteById(id);
        logger.info("Registro de pago ID={} eliminado correctamente", id);
    }

    // MÉTODOS DE APOYO LÓGICO (INTERNOS)
   
    private void validarQueNoEstePagada(Long idCompra) {
        if (pagoRepository.existsByIdCompra(idCompra)) {
            logger.warn("Doble cobro detectado para compra ID={}", idCompra);
            throw new BadRequestException("La compra ya se encuentra pagada.");
        }
    }

    private int obtenerDescuentoDelCupon(String codigoPromocion, String tokenAuth) {
        if (codigoPromocion == null || codigoPromocion.isBlank()) {
            return 0;
        }

        PromocionResponseDTO promoResult = webClient.get()
                .uri(promocionUrl + "/validar/" + codigoPromocion)
                .header("Authorization", tokenAuth)
                .retrieve()
                .bodyToMono(PromocionResponseDTO.class)
                .block();

        if (promoResult == null || !promoResult.isEsValido()) {
            String motivo = (promoResult != null) ? promoResult.getMensaje() : "Servicio no disponible.";
            throw new BadRequestException("Cupón inválido: " + motivo);
        }

        return promoResult.getPorcentajeDescuento();
    }

    private CompraDTO obtenerDatosCompra(Long idCompra, String tokenAuth) {
        CompraDTO compra = webClient.get()
                .uri(compraUrl + "/" + idCompra)
                .header("Authorization", tokenAuth)
                .retrieve()
                .bodyToMono(CompraDTO.class)
                .block();

        if (compra == null) {
            throw new ResourceNotFoundException("La compra solicitada no existe.");
        }
        return compra;
    }

    private EscenarioDTO obtenerDatosEscenario(Long escenarioId, String tokenAuth) {
        EscenarioDTO escenario = webClient.get()
                .uri(escenarioUrl + "/" + escenarioId)
                .header("Authorization", tokenAuth)
                .retrieve()
                .bodyToMono(EscenarioDTO.class)
                .block();

        if (escenario == null) {
            throw new ResourceNotFoundException("El escenario asociado no existe.");
        }
        return escenario;
    }

    private Pago calcularYConstruirPago(Long usuarioId, PagoRequestDTO request, CompraDTO compra, EscenarioDTO escenario, int porcentajeDescuento) {
        int montoBase = compra.getCantidad() * escenario.getPrecio();
        int montoDescuento = calcularMontoDescuento(montoBase, porcentajeDescuento);
        int subtotalConDescuento = calcularSubtotalConDescuento(montoBase, montoDescuento);
        int iva = calcularIVA(subtotalConDescuento);
        int montoTotal = calcularTotal(subtotalConDescuento, iva);

        if (montoTotal <= 0) {
            throw new BadRequestException("El total a pagar debe ser mayor a 0.");
        }

        Pago pago = new Pago();
        pago.setUsuarioId(usuarioId);
        pago.setIdCompra(request.getIdCompra());
        pago.setMontoSubtotal(montoBase);
        pago.setPorcentajeDescuento(porcentajeDescuento);
        pago.setMontoDescuento(montoDescuento);
        pago.setMontoNeto(subtotalConDescuento);
        pago.setIva(iva);
        pago.setMontoTotal(montoTotal);
        pago.setMedioPago(request.getMedioPago());
        pago.setFechaPago(LocalDateTime.now());
        
        return pago;
    }

    // MÉTODOS DE CÁLCULO MATEMÁTICO
  
    public int calcularMontoDescuento(int montoBase, int porcentajeDescuento) {
        if (montoBase < 0 || porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new BadRequestException("Parámetros matemáticos inválidos.");
        }
        return (int) (montoBase * (porcentajeDescuento / 100.0));
    }

    public int calcularSubtotalConDescuento(int montoBase, int montoDescuento) {
        return montoBase - montoDescuento;
    }

    public int calcularIVA(int subtotalConDescuento) {
        if (subtotalConDescuento < 0) {
            throw new BadRequestException("El subtotal no puede ser negativo");
        }
        return (int) (subtotalConDescuento * 0.19);
    }

    public int calcularTotal(int subtotalConDescuento, int iva) {
        return subtotalConDescuento + iva;
    }
}