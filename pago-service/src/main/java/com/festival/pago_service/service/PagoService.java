package com.festival.pago_service.service;

import com.festival.pago_service.dto.CompraDTO;
import com.festival.pago_service.dto.EscenarioDTO;
import com.festival.pago_service.dto.PagoRequestDTO;
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

@Service
public class PagoService {

    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);
    private final PagoRepository pagoRepository;
    private final WebClient webClient;

    @Value("${api.compra.url}")
    private String compraUrl;

    @Value("${api.escenario.url}")
    private String escenarioUrl;

    public PagoService(PagoRepository pagoRepository, WebClient.Builder webClientBuilder) {
        this.pagoRepository = pagoRepository;
        this.webClient = webClientBuilder.build();
    }


    public Pago procesarPago(PagoRequestDTO request, String tokenAuth, Long usuarioId) {
        logger.info("Iniciando procesamiento de pago para la compra ID={}, MedioPago={}, Dcto={}%", 
                request.getIdCompra(), request.getMedioPago(), request.getPorcentajeDescuento());

        try {
            // 1. Validar duplicados
            if (pagoRepository.existsByIdCompra(request.getIdCompra())) {
                logger.warn("Intento de doble cobro detectado. La compra ID={} ya está pagada.", request.getIdCompra());
                throw new BadRequestException("La compra ya se encuentra pagada. No se puede procesar el pago nuevamente.");
            }

            // 2. Obtener Compra
            logger.info("Realizando petición al microservicio de compras URI: {}", compraUrl + "/" + request.getIdCompra());
            CompraDTO compra = webClient.get()
                    .uri(compraUrl + "/" + request.getIdCompra())
                    .header("Authorization", tokenAuth)
                    .retrieve()
                    .bodyToMono(CompraDTO.class)
                    .block();

            if (compra == null) {
                logger.error("No se pudo obtener la compra ID={}", request.getIdCompra());
                throw new ResourceNotFoundException("La compra solicitada no existe o el servicio no está disponible.");
            }

            // 3. Obtener Escenario (Precio)
            logger.info("Realizando petición al microservicio de escenarios URI: {}", escenarioUrl + "/" + compra.getEscenarioId());
            EscenarioDTO escenario = webClient.get()
                    .uri(escenarioUrl + "/" + compra.getEscenarioId())
                    .header("Authorization", tokenAuth)
                    .retrieve()
                    .bodyToMono(EscenarioDTO.class)
                    .block();

            if (escenario == null) {
                logger.error("No se pudo obtener el escenario ID={}", compra.getEscenarioId());
                throw new ResourceNotFoundException("El escenario asociado a esta compra no existe.");
            }

            // 4. Cálculos Matemáticos
            int montoBase = compra.getCantidad() * escenario.getPrecio();
            int montoDescuento = calcularMontoDescuento(montoBase, request.getPorcentajeDescuento());
            int subtotalConDescuento = calcularSubtotalConDescuento(montoBase, montoDescuento);
            int iva = calcularIVA(subtotalConDescuento);
            int montoTotal = calcularTotal(subtotalConDescuento, iva);

            if (montoTotal <= 0) {
                throw new BadRequestException("El total a pagar debe ser mayor a 0.");
            }

            // 5. Guardar
            Pago nuevoPago = new Pago();
            nuevoPago.setUsuarioId(usuarioId); // <-- Guardamos la huella del dueño
            nuevoPago.setIdCompra(request.getIdCompra());
            nuevoPago.setMontoSubtotal(montoBase);
            nuevoPago.setPorcentajeDescuento(request.getPorcentajeDescuento());
            nuevoPago.setMontoDescuento(montoDescuento);
            nuevoPago.setIva(iva);
            nuevoPago.setMontoTotal(montoTotal);
            nuevoPago.setMedioPago(request.getMedioPago());
            nuevoPago.setFechaPago(LocalDateTime.now());

            Pago pagoGuardado = pagoRepository.save(nuevoPago);
            logger.info("Pago guardado exitosamente con ID={} por un total de ${}", pagoGuardado.getId(), montoTotal);
            return pagoGuardado;

        } catch (Exception e) {
            logger.error("Error al procesar el pago para la compra ID={}: {}", request.getIdCompra(), e.getMessage(), e);
            throw e;
        }
    }

  
    public List<Pago> listarTodos() {
        logger.info("Listando todos los pagos");
        List<Pago> pagos = pagoRepository.findAll();
        logger.info("Total pagos encontrados: {}", pagos.size());
        return pagos;
    }

   
    public Pago buscarPorId(Long id) {
        logger.info("Buscando pago por ID={}", id);
        return pagoRepository.findById(id).orElseThrow(() -> {
            logger.warn("Pago no encontrado ID={}", id);
            return new ResourceNotFoundException("El registro de pago solicitado no existe.");
        });
    }

    public Pago actualizar(Long id, PagoRequestDTO request) {
        logger.info("Iniciando actualización de pago ID={}", id);
        try {
            Pago existente = buscarPorId(id);
            
            existente.setMedioPago(request.getMedioPago());
            
            Pago actualizado = pagoRepository.save(existente);
            logger.info("Pago actualizado exitosamente ID={}", actualizado.getId());
            return actualizado;
        } catch (Exception e) {
            logger.error("Error al actualizar pago ID={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

   
    public void eliminar(Long id) {
        logger.info("Iniciando eliminación de pago ID={}", id);
        try {
            if (!pagoRepository.existsById(id)) {
                logger.warn("Pago no existe para eliminar ID={}", id);
                throw new ResourceNotFoundException("No se puede eliminar: el pago no existe.");
            }
            pagoRepository.deleteById(id);
            logger.info("Pago eliminado exitosamente ID={}", id);
        } catch (Exception e) {
            logger.error("Error al eliminar pago ID={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    // --- MÉTODOS MATEMÁTICOS --- 

    public int calcularMontoDescuento(int montoBase, int porcentajeDescuento) {
        if (montoBase < 0) throw new BadRequestException("El monto base no puede ser negativo");
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) throw new BadRequestException("El descuento debe estar entre 0 y 100");
        return (int) (montoBase * (porcentajeDescuento / 100.0));
    }

    public int calcularSubtotalConDescuento(int montoBase, int montoDescuento) {
        return montoBase - montoDescuento;
    }

    public int calcularIVA(int subtotalConDescuento) {
        if (subtotalConDescuento < 0) throw new BadRequestException("El subtotal no puede ser negativo");
        return (int) (subtotalConDescuento * 0.19);
    }

    public int calcularTotal(int subtotalConDescuento, int iva) {
        return subtotalConDescuento + iva;
    }
}