package com.festival.presentacion_service.service;

import com.festival.presentacion_service.dto.ArtistaDTO;
import com.festival.presentacion_service.dto.EscenarioDTO;
import com.festival.presentacion_service.dto.PresentacionResponseDTO;
import com.festival.presentacion_service.exception.BadRequestException;
import com.festival.presentacion_service.exception.ResourceNotFoundException;
import com.festival.presentacion_service.model.Presentacion;
import com.festival.presentacion_service.repository.PresentacionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PresentacionService {

    private static final Logger logger = LoggerFactory.getLogger(PresentacionService.class);

    private final PresentacionRepository presentacionRepository;
    private final WebClient webClient;

    @Value("${api.artista.exists}")
    private String artistaPath;

    @Value("${api.escenario.exists}")
    private String escenarioPath;

    @Value("${api.artista.idsByGenero}")
    private String artistaIdsByGeneroPath;

    @Value("${api.artista.detalle}")
    private String artistaDetallePath;

    @Value("${api.escenario.detalle}")
    private String escenarioDetallePath;

    public PresentacionService(PresentacionRepository presentacionRepository, WebClient webClient) {
        this.presentacionRepository = presentacionRepository;
        this.webClient = webClient;
    }

    public List<Presentacion> listar() {
        logger.info("Listando todas las presentaciones");
        List<Presentacion> presentaciones = presentacionRepository.findAll();
        logger.debug("Cantidad de presentaciones encontradas: {}", presentaciones.size());
        return presentaciones;
    }

    public Presentacion buscarPorId(Long id) {
        logger.info("Buscando presentación por id={}", id);
        return presentacionRepository.findById(id)
         .orElseThrow(() -> {
            logger.warn("Busqueda fallida: Presentación id={} no encontrada", id);
            return new ResourceNotFoundException("No se encontró la presentación con id: " + id);
        });
    }

    public boolean existePorId(Long id){
        return presentacionRepository.existsById(id);
    }

    public Presentacion guardar(Presentacion presentacion) {
        logger.info("Iniciando guardado de presentación: artistaId={}, escenarioId={}",
                presentacion.getArtistaId(), presentacion.getEscenarioId());

        validarDependencias(presentacion.getArtistaId(), presentacion.getEscenarioId());

        Presentacion guardada = presentacionRepository.save(presentacion);
        logger.info("Presentación guardada exitosamente id={}", guardada.getId());
        return guardada;
    }

    public Presentacion actualizar(Long id, Presentacion detallesNuevos) {
        logger.info("Iniciando actualización de presentación id={}", id);

        Presentacion presentacionExistente = buscarPorId(id);
        
            logger.debug("Presentación id={} encontrada. Validando nuevos datos: artistaId={}, escenarioId={}",
                    id, detallesNuevos.getArtistaId(), detallesNuevos.getEscenarioId());

            validarDependencias(detallesNuevos.getArtistaId(), detallesNuevos.getEscenarioId());

            presentacionExistente.setArtistaId(detallesNuevos.getArtistaId());
            presentacionExistente.setEscenarioId(detallesNuevos.getEscenarioId());
            presentacionExistente.setFechaHora(detallesNuevos.getFechaHora());
            presentacionExistente.setDuracionMinutos(detallesNuevos.getDuracionMinutos());
            
            Presentacion actualizada = presentacionRepository.save(presentacionExistente);
            logger.info("Presentación id={} actualizada exitosamente", actualizada.getId());
            return actualizada;
       
    }

    public void eliminar(Long id) {
        logger.info("Intentando eliminar presentación id={}", id);
        if (!presentacionRepository.existsById(id)) {
            logger.warn("No se pudo eliminar: presentación id={} no existe", id);
            throw new ResourceNotFoundException("No se encontró la presentación con id: " + id);
        }
        presentacionRepository.deleteById(id);
        logger.info("Presentación id={} eliminada exitosamente", id);
    }

    private void validarDependencias(Long artistaId, Long escenarioId) {
        Boolean existeArtista = validarExistencia(artistaPath, artistaId, "artista");
        if (!Boolean.TRUE.equals(existeArtista)) {
            logger.warn("Validación fallida: Artista id={} no existe", artistaId);
            throw new ResourceNotFoundException("El artista no existe");
        }

        Boolean existeEscenario = validarExistencia(escenarioPath, escenarioId, "escenario");
        if (!Boolean.TRUE.equals(existeEscenario)) {
            logger.warn("Validación fallida: Escenario id={} no existe", escenarioId);
            throw new ResourceNotFoundException("El escenario no existe");
        }
    }

    private Boolean validarExistencia(String path, Long id, String tipo) {
        try {
            logger.debug("Validando existencia de {} id={}", tipo, id);
            Boolean existe = webClient.get()
                    .uri(String.format(path, id))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            logger.debug("Respuesta existencia {}: {}", tipo, existe);
            return existe;
        } catch (Exception e) {
            logger.error("Error de comunicación al validar {} id={}", tipo, id, e);
            throw new BadRequestException("Error al conectar con el servicio de " + tipo);
        }
    }

    public List<Presentacion> buscarPorDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        logger.info("Buscando presentaciones para el día completo: {} al {}", inicio, fin);
        return presentacionRepository.findByFechaHoraBetween(inicio, fin);
    }

    public List<Presentacion> buscarPorGeneroMusical(String genero) {
        logger.info("Iniciando búsqueda de presentaciones para el género: {}", genero);
        try {
            List<Long> artistaIds = webClient.get()
                    .uri(String.format(artistaIdsByGeneroPath, genero))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Long>>() {})
                    .block();

            if (artistaIds == null || artistaIds.isEmpty()) {
                logger.info("No se encontraron artistas para el género: {}", genero);
                return Collections.emptyList();
            }

            logger.debug("IDs de artistas encontrados para {}: {}", genero, artistaIds);
            return presentacionRepository.findAllByArtistaIdIn(artistaIds);
        } catch (Exception e) {
            logger.error("Error al comunicarse con Artista Service para buscar género: {}", genero, e);
            throw new BadRequestException("No se pudo obtener la información de géneros musicales");
        }
    }

    public PresentacionResponseDTO obtenerDetalle(Presentacion p) {
        logger.debug("Enriqueciendo datos de presentación ID {} con servicios externos", p.getId());
        
        ArtistaDTO artista = null;
        try {
            artista = webClient.get()
                    .uri(String.format(artistaDetallePath, p.getArtistaId()))
                    .retrieve()
                    .bodyToMono(ArtistaDTO.class)
                    .block();
        } catch (Exception e) {
            logger.error("No se pudo obtener el detalle del artista ID {}: {}", p.getArtistaId(), e.getMessage());
        }

        EscenarioDTO escenario = null;
        try {
            escenario = webClient.get()
                    .uri(String.format(escenarioDetallePath, p.getEscenarioId()))
                    .retrieve()
                    .bodyToMono(EscenarioDTO.class)
                    .block();
        } catch (Exception e) {
            logger.error("No se pudo obtener el detalle del escenario ID {}: {}", p.getEscenarioId(), e.getMessage());
        }

        if (escenario != null && escenario.getZona() == null) {
            logger.warn("La información de zona no está disponible para el escenario ID {}", p.getEscenarioId());
        }     

        return PresentacionResponseDTO.builder()
                .id(p.getId())
                .artistaId(p.getArtistaId())
                .escenarioId(p.getEscenarioId())
                .nombreArtista(artista != null ? artista.getNombre() : "Artista no disponible")
                .nombreEscenario(escenario != null ? escenario.getNombre() : "Escenario no disponible")
                .precioEscenario(escenario != null ? escenario.getPrecio() : null)
                .puertaAcceso(escenario != null ? escenario.getPuertaAcceso() : "Acceso no disponible")
                .nombreZona(escenario != null && escenario.getZona() != null ? escenario.getZona().getNombre() : "Zona no disponible")
                .fechaHora(p.getFechaHora())
                .duracionMinutos(p.getDuracionMinutos())
                .build();
    }
}