package com.festival.itinerario_service.service;

import com.festival.itinerario_service.dto.ItinerarioResponseDTO;
import com.festival.itinerario_service.dto.PresentacionDTO;
import com.festival.itinerario_service.exception.BadRequestException;
import com.festival.itinerario_service.exception.ResourceNotFoundException;
import com.festival.itinerario_service.model.Itinerario;
import com.festival.itinerario_service.repository.ItinerarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItinerarioService {

    private static final Logger logger = LoggerFactory.getLogger(ItinerarioService.class);

    private final ItinerarioRepository itinerarioRepository;
    private final WebClient webClient;

    @Value("${api.usuario.exists}")
    private String usuarioPath;

    @Value("${api.presentacion.exists}")
    private String presentacionPath;

    @Value("${api.presentacion.detalle}")
    private String presentacionDetallePath;

    public ItinerarioService(ItinerarioRepository itinerarioRepository, WebClient webClient) {
        this.itinerarioRepository = itinerarioRepository;
        this.webClient = webClient;
    }

    // Método necesario para validar la propiedad en el controlador
    public Optional<Itinerario> buscarPorId(Long id) {
        return itinerarioRepository.findById(id);
    }

    public Itinerario guardar(Itinerario itinerario) {
        logger.info("Iniciando validación de itinerario: usuarioId={}, presentacionId={}", 
                    itinerario.getUsuarioId(), itinerario.getPresentacionId());

        // 1. Validar Usuario
        Boolean existeUsuario = validarExistencia(usuarioPath, itinerario.getUsuarioId(), "usuario");
        if (!Boolean.TRUE.equals(existeUsuario)) {
            logger.warn("El usuario ID={} no existe", itinerario.getUsuarioId());
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        // 2. Validar Presentación
        Boolean existePresentacion = validarExistencia(presentacionPath, itinerario.getPresentacionId(), "presentación");
        if (!Boolean.TRUE.equals(existePresentacion)) {
            logger.warn("La presentación ID={} no existe", itinerario.getPresentacionId());
            throw new ResourceNotFoundException("Presentación no encontrada");
        }

        // 3. Validar duplicado
        if (itinerarioRepository.existsByUsuarioIdAndPresentacionId(itinerario.getUsuarioId(), itinerario.getPresentacionId())) {
            logger.warn("Bloqueo: El usuario {} ya tiene la presentación {} en su itinerario", 
                        itinerario.getUsuarioId(), itinerario.getPresentacionId());
            throw new BadRequestException("Esta presentación ya está en tu itinerario.");
        }

        // --- MODIFICACIÓN AQUÍ: Le pasamos 'null' porque es un guardado nuevo, no hay nada que ignorar ---
        validarCruceHorarios(itinerario.getUsuarioId(), itinerario.getPresentacionId(), null);

        // 5. Guardado final
        itinerario.setFechaAgregado(LocalDateTime.now());
        Itinerario guardado = itinerarioRepository.save(itinerario);
        
        logger.info("Itinerario guardado con éxito. ID: {}", guardado.getId());
        return guardado;
    }

    private Boolean validarExistencia(String path, Long id, String tipo) {
        try {
            logger.debug("Llamando a {} para validar ID: {}", tipo, id);
            return webClient.get()
                    .uri(String.format(path, id))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(); 
        } catch (Exception e) {
            logger.error("Error de comunicación al validar {}: {}", tipo, e.getMessage());
            throw new BadRequestException("Servicio de " + tipo + " no disponible");
        }
    }

    public List<Itinerario> listarPorUsuario(Long usuarioId) {
        logger.info("Listando itinerario para usuarioId={}", usuarioId);
        return itinerarioRepository.findByUsuarioId(usuarioId);
    }

    public List<Itinerario> listarTodos() {
        logger.info("Listando todos los itinerarios del sistema");
        return itinerarioRepository.findAll();
    }
    
    // --- MODIFICACIÓN AQUÍ: Método actualizar robustecido ---
    public Optional<Itinerario> actualizar(Long id, Itinerario detallesNuevos) {
        logger.info("Iniciando actualización de itinerario ID: {}", id);

        return itinerarioRepository.findById(id).map(itinerarioExistente -> {
            
            // 1. Validar existencia básica en otros microservicios
            Boolean existeUsuario = validarExistencia(usuarioPath, detallesNuevos.getUsuarioId(), "usuario");
            Boolean existePresentacion = validarExistencia(presentacionPath, detallesNuevos.getPresentacionId(), "presentación");

            if (!Boolean.TRUE.equals(existeUsuario) || !Boolean.TRUE.equals(existePresentacion)) {
                logger.warn("No se pudo actualizar: uno de los IDs proporcionados no existe");
                throw new ResourceNotFoundException("Los IDs de usuario o presentación no son válidos");
            }

            // 2. Validar duplicado SOLO si el usuario está intentando cambiar la presentación actual por una distinta
            if (!itinerarioExistente.getPresentacionId().equals(detallesNuevos.getPresentacionId()) &&
                itinerarioRepository.existsByUsuarioIdAndPresentacionId(detallesNuevos.getUsuarioId(), detallesNuevos.getPresentacionId())) {
                throw new BadRequestException("Esta presentación ya está en tu itinerario.");
            }

            // 3. Validar cruce de horarios pasándole el ID actual para que NO choque consigo mismo
            validarCruceHorarios(detallesNuevos.getUsuarioId(), detallesNuevos.getPresentacionId(), id);

            // 4. Aplicar cambios y guardar
            itinerarioExistente.setUsuarioId(detallesNuevos.getUsuarioId());
            itinerarioExistente.setPresentacionId(detallesNuevos.getPresentacionId());
            
            Itinerario actualizado = itinerarioRepository.save(itinerarioExistente);
            logger.info("Itinerario ID: {} actualizado correctamente", id);
            return actualizado;
        });
    }

    public boolean eliminar(Long id) {
        logger.info("Intentando eliminar itinerario ID: {}", id);
        if (itinerarioRepository.existsById(id)) {
            itinerarioRepository.deleteById(id);
            logger.info("Itinerario ID: {} eliminado con éxito", id);
            return true;
        }
        logger.warn("No se pudo eliminar: el itinerario ID: {} no existe", id);
        return false;
    }

    public ItinerarioResponseDTO obtenerDetalleEnriquecido(Itinerario itinerario) {
        logger.debug("Enriqueciendo itinerario ID {} con datos de presentación ID {}", 
                     itinerario.getId(), itinerario.getPresentacionId());

        PresentacionDTO presentacionDetalle = null;
        try {
            presentacionDetalle = webClient.get()
                    .uri(String.format(presentacionDetallePath, itinerario.getPresentacionId()))
                    .retrieve()
                    .bodyToMono(PresentacionDTO.class) 
                    .block();
        } catch (Exception e) {
            logger.error("No se pudo obtener el detalle de la presentación ID {}: {}", 
                         itinerario.getPresentacionId(), e.getMessage());
        }

        return ItinerarioResponseDTO.builder()
                .id(itinerario.getId())
                .usuarioId(itinerario.getUsuarioId())
                .fechaAgregado(itinerario.getFechaAgregado())
                .presentacion(presentacionDetalle) 
                .build();
    }

    // --- MODIFICACIÓN AQUÍ: Se agregó el tercer parámetro (itinerarioIdIgnorar) ---
    private void validarCruceHorarios(Long usuarioId, Long nuevaPresentacionId, Long itinerarioIdIgnorar) {
        logger.debug("Validando cruce de horarios para usuarioId={} y nueva presentacionId={}", usuarioId, nuevaPresentacionId);

        PresentacionDTO nuevaPres = null;
        try {
            nuevaPres = webClient.get()
                    .uri(String.format(presentacionDetallePath, nuevaPresentacionId))
                    .retrieve()
                    .bodyToMono(PresentacionDTO.class)
                    .block();
        } catch (Exception e) {
            logger.warn("No se pudo validar el horario porque la presentación {} no se pudo recuperar", nuevaPresentacionId);
            return; 
        }

        if (nuevaPres == null || nuevaPres.getFechaHora() == null) return;

        LocalDateTime inicioNuevo = nuevaPres.getFechaHora();
        LocalDateTime finNuevo = inicioNuevo.plusMinutes(nuevaPres.getDuracionMinutos());

        List<Itinerario> itinerariosActuales = itinerarioRepository.findByUsuarioId(usuarioId);

        for (Itinerario it : itinerariosActuales) {
            
            // --- LA MAGIA ESTÁ AQUÍ: Ignoramos el registro que estamos actualizando ---
            if (itinerarioIdIgnorar != null && it.getId().equals(itinerarioIdIgnorar)) {
                continue; 
            }

            try {
                PresentacionDTO presExistente = webClient.get()
                        .uri(String.format(presentacionDetallePath, it.getPresentacionId()))
                        .retrieve()
                        .bodyToMono(PresentacionDTO.class)
                        .block();

                if (presExistente != null && presExistente.getFechaHora() != null) {
                    LocalDateTime inicioExistente = presExistente.getFechaHora();
                    LocalDateTime finExistente = inicioExistente.plusMinutes(presExistente.getDuracionMinutos());

                    if (inicioNuevo.isBefore(finExistente) && finNuevo.isAfter(inicioExistente)) {
                        logger.warn("Cruce detectado: El usuario {} intentó agregar {} pero choca con {}", 
                                usuarioId, nuevaPres.getNombreArtista(), presExistente.getNombreArtista());
                        
                        throw new BadRequestException(
                                String.format("Cruce de horarios detectado: No puedes ir a ver a %s porque a esa misma hora ya tienes agendado el show de %s.", 
                                nuevaPres.getNombreArtista(), presExistente.getNombreArtista())
                        );
                    }
                }
            } catch (BadRequestException e) {
                throw e; 
            } catch (Exception e) {
                logger.error("Error al validar presentación existente ID {}: {}", it.getPresentacionId(), e.getMessage());
            }
        }
    }
}