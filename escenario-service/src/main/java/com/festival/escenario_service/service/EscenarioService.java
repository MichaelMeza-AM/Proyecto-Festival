package com.festival.escenario_service.service;

import com.festival.escenario_service.exception.ResourceNotFoundException;
import com.festival.escenario_service.model.Escenario;
import com.festival.escenario_service.model.Zona;
import com.festival.escenario_service.repository.EscenarioRepository;
import com.festival.escenario_service.repository.ZonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EscenarioService {

    private static final Logger logger = LoggerFactory.getLogger(EscenarioService.class);
    private final EscenarioRepository escenarioRepository;
    private final ZonaRepository zonaRepository;

    public EscenarioService(EscenarioRepository escenarioRepository, ZonaRepository zonaRepository) {
        this.escenarioRepository = escenarioRepository;
        this.zonaRepository = zonaRepository;
    }

    public List<Escenario> listar() {
        logger.info("Listando todos los escenarios");
        return escenarioRepository.findAll();
    }

    public Escenario buscarPorId(Long id) {
        logger.info("Buscando escenario con ID: {}", id);
        return escenarioRepository.findById(id)
         .orElseThrow(() -> {
             logger.warn("Busqueda fallida: Escenario id={} no encontrado", id);
             return new ResourceNotFoundException("No se encontró el escenario con ID " + id);
         });
    }
    
    public boolean existePorId(Long id) {
        logger.info("Verificando existencia de escenario con ID: {}", id);
        return escenarioRepository.existsById(id);
    }

    public Escenario guardar(Escenario escenario) {
        logger.info("Iniciando guardado de nuevo escenario: {}", escenario.getNombre());
        
        Zona zonaReal = zonaRepository.findById(escenario.getZona().getId())
                .orElseThrow(() -> {
                    logger.warn("Validacion fallida: Zona id={} no encontrada", escenario.getZona().getId());
                    return new ResourceNotFoundException("No se puede crear. La zona geográfica con ID " + escenario.getZona().getId() + " no existe.");
                });
        
        escenario.setZona(zonaReal);
        Escenario guardado = escenarioRepository.save(escenario);
        logger.info("Escenario guardado exitosamente id={}", guardado.getId());
        return guardado;
    }

    public Escenario actualizar(Long id, Escenario detallesNuevos) {
        logger.info("Iniciando actualización de escenario id={}", id);
        Escenario escenarioExistente = buscarPorId(id);

           logger.debug("Escenario id={} encontrado. Validando nueva zona id={}", id, detallesNuevos.getZona().getId());
            Zona zonaReal = zonaRepository.findById(detallesNuevos.getZona().getId())
                    .orElseThrow(() -> {
                        logger.warn("Validación fallida: Zona id={} no existe al intentar actualizar escenario id={}", detallesNuevos.getZona().getId(), id);
                        return new ResourceNotFoundException("No se puede actualizar. La zona geográfica con ID " + detallesNuevos.getZona().getId() + " no existe.");
                    });

            escenarioExistente.setNombre(detallesNuevos.getNombre());
            escenarioExistente.setPuertaAcceso(detallesNuevos.getPuertaAcceso());
            escenarioExistente.setAforoMaximo(detallesNuevos.getAforoMaximo());
            escenarioExistente.setPrecio(detallesNuevos.getPrecio()); 
          
            escenarioExistente.setZona(zonaReal); 
            
            Escenario actualizado = escenarioRepository.save(escenarioExistente);
            logger.info("Escenario id={} actualizado exitosamente", actualizado.getId());
            return actualizado;
        
    }

    public void eliminar(Long id) {
        logger.info("Intentando eliminar escenario id={}", id);
        if (!escenarioRepository.existsById(id)) {
            logger.warn("No se pudo eliminar: escenario id={} no existe", id);
            throw new ResourceNotFoundException("No se puede eliminar. El escenario con ID " + id + " no existe.");
        }
        escenarioRepository.deleteById(id);
        logger.info("Escenario id={} eliminado exitosamente", id);
    }
}