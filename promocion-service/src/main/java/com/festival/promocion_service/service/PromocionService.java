package com.festival.promocion_service.service;

import com.festival.promocion_service.dto.PromocionResponseDTO;
import com.festival.promocion_service.exception.BadRequestException;
import com.festival.promocion_service.exception.ResourceNotFoundException;
import com.festival.promocion_service.model.Promocion;
import com.festival.promocion_service.repository.PromocionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromocionService {

    private static final Logger logger = LoggerFactory.getLogger(PromocionService.class);
    private final PromocionRepository promocionRepository;

    public PromocionService(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    public Promocion guardar(Promocion promocion) {
        logger.info("Iniciando registro de nueva promoción con código: {}", promocion.getCodigo());

        if (promocionRepository.findByCodigo(promocion.getCodigo()).isPresent()) {
            logger.warn("Intento de duplicación detectado. El código {} ya existe.", promocion.getCodigo());
            throw new BadRequestException("El código de promoción '" + promocion.getCodigo() + "' ya se encuentra registrado.");
        }

        if (promocion.getFechaFin().isBefore(promocion.getFechaInicio())) {
            logger.warn("Error en consistencia de fechas: Inicio={}, Fin={}", promocion.getFechaInicio(), promocion.getFechaFin());
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        Promocion guardada = promocionRepository.save(promocion);
        logger.info("Promoción guardada exitosamente con ID={}", guardada.getId());
        return guardada;
    }

    public List<Promocion> listarTodas() {
        logger.info("Listando todas las promociones del festival");
        List<Promocion> promociones = promocionRepository.findAll();
        logger.info("Total de promociones encontradas: {}", promociones.size());
        return promociones;
    }

    public Promocion buscarPorId(Long id) {
        logger.info("Buscando promoción por ID={}", id);
        return promocionRepository.findById(id).orElseThrow(() -> {
            logger.warn("No se encontró la promoción con ID={}", id);
            return new ResourceNotFoundException("Promoción no encontrada con id: " + id);
        });
    }

    public boolean existePorId(Long id) {
        return promocionRepository.existsById(id);
    }
 
    public Promocion actualizar(Long id, Promocion detallesNuevos) {
        logger.info("Iniciando proceso de actualización para la promoción ID={}", id);

        Promocion promocionExistente = buscarPorId(id);

        if (detallesNuevos.getFechaFin().isBefore(detallesNuevos.getFechaInicio())) {
            logger.warn("Error al actualizar ID={}: La fecha de fin ({}) es anterior a la de inicio ({}).", 
                    id, detallesNuevos.getFechaFin(), detallesNuevos.getFechaInicio());
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        // Validar el código de texto solo si el usuario intentó modificarlo
        if (!promocionExistente.getCodigo().equalsIgnoreCase(detallesNuevos.getCodigo())) {
            // Si el código es distinto, verificamos que el nuevo no esté usado por otra promoción
            if (promocionRepository.findByCodigo(detallesNuevos.getCodigo()).isPresent()) {
                logger.warn("Validación de actualización fallida: El código '{}' ya está en uso.", detallesNuevos.getCodigo());
                throw new BadRequestException("No se puede actualizar. El código de promoción '" + detallesNuevos.getCodigo() + "' ya existe.");
            }
        }

        promocionExistente.setCodigo(detallesNuevos.getCodigo());
        promocionExistente.setPorcentajeDescuento(detallesNuevos.getPorcentajeDescuento());
        promocionExistente.setFechaInicio(detallesNuevos.getFechaInicio());
        promocionExistente.setFechaFin(detallesNuevos.getFechaFin());
        
        if (detallesNuevos.getActivo() != null) {
            promocionExistente.setActivo(detallesNuevos.getActivo());
        }

        Promocion actualizada = promocionRepository.save(promocionExistente);
        logger.info("Promoción ID={} actualizada exitosamente en el sistema.", actualizada.getId());
        return actualizada;
    }

    public void eliminar(Long id) {
        logger.info("Iniciando proceso de eliminación para la promoción ID={}", id);
        if (!promocionRepository.existsById(id)) {
            logger.warn("Cancelando eliminación: La promoción ID={} no existe.", id);
            throw new ResourceNotFoundException("No se puede eliminar. La promoción con id: " + id + " no existe.");
        }
        promocionRepository.deleteById(id);
        logger.info("Promoción ID={} eliminada correctamente de la base de datos", id);
    }

    /* Valida si un cupón ingresado por el cliente es apto para aplicar descuentos analizando fechas actuales y estados. */
    public PromocionResponseDTO validarPromocion(String codigo) {
        logger.info("Procesando solicitud de validación para el código: {}", codigo);

        // 1. Intentar recuperar el registro por su código único
        Promocion promocion = promocionRepository.findByCodigo(codigo)
                .orElseThrow(() -> {
                    logger.warn("Validación rechazada: El código '{}' no existe en el sistema.", codigo);
                    return new ResourceNotFoundException("El código de promoción '" + codigo + "' no existe.");
                });

        PromocionResponseDTO response = PromocionResponseDTO.fromModel(promocion);
        LocalDateTime ahora = LocalDateTime.now();

        // 2. Verificar si está desactivada explícitamente por el administrador
        if (!promocion.getActivo()) {
            logger.warn("Código '{}' rechazado: Se encuentra marcado como inactivo.", codigo);
            response.setEsValido(false);
            response.setMensaje("La promoción se encuentra desactivada temporalmente.");
            return response;
        }

        // 3. Verificar si el festival/evento asociado aún no comienza
        if (ahora.isBefore(promocion.getFechaInicio())) {
            logger.warn("Código '{}' rechazado: No vigente aún. Inicia el: {}", codigo, promocion.getFechaInicio());
            response.setEsValido(false);
            response.setMensaje("La promoción aún no está vigente. Válida desde: " + promocion.getFechaInicio());
            return response;
        }

        // 4. Verificar si el cupón ya caducó en el tiempo
        if (ahora.isAfter(promocion.getFechaFin())) {
            logger.warn("Código '{}' rechazado: Expirado. Finalizó el: {}", codigo, promocion.getFechaFin());
            response.setEsValido(false);
            response.setMensaje("La promoción ha expirado. Finalizó el: " + promocion.getFechaFin());
            return response;
        }

        // 5. Si superó todos los filtros anteriores, el cupón es plenamente válido
        logger.info("Código '{}' aprobado exitosamente. Descuento aplicable: {}%", codigo, promocion.getPorcentajeDescuento());
        response.setEsValido(true);
        response.setMensaje("¡Promoción aplicada con éxito! Se otorga un " + promocion.getPorcentajeDescuento() + "% de descuento.");
        return response;
    }
}