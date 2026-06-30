package com.festival.escenario_service.service;

import com.festival.escenario_service.exception.ResourceNotFoundException;
import com.festival.escenario_service.model.Escenario;
import com.festival.escenario_service.model.Zona;
import com.festival.escenario_service.repository.EscenarioRepository;
import com.festival.escenario_service.repository.ZonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EscenarioService {

    private final EscenarioRepository escenarioRepository;
    private final ZonaRepository zonaRepository;

    public EscenarioService(EscenarioRepository escenarioRepository, ZonaRepository zonaRepository) {
        this.escenarioRepository = escenarioRepository;
        this.zonaRepository = zonaRepository;
    }

    public List<Escenario> listar() {
        return escenarioRepository.findAll();
    }

    public Escenario buscarPorId(Long id) {
        return escenarioRepository.findById(id)
         .orElseThrow(() -> new ResourceNotFoundException("No se encontró el escenario con ID " + id));
    }
    
    public boolean existePorId(Long id) {
        return escenarioRepository.existsById(id);
    }

    public Escenario guardar(Escenario escenario) {
        Zona zonaReal = zonaRepository.findById(escenario.getZona().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear. La zona geográfica con ID " + escenario.getZona().getId() + " no existe."));
        
        escenario.setZona(zonaReal);
        return escenarioRepository.save(escenario);
    }

    public Escenario actualizar(Long id, Escenario detallesNuevos) {
        Escenario escenarioExistente = buscarPorId(id);

            Zona zonaReal = zonaRepository.findById(detallesNuevos.getZona().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar. La zona geográfica con ID " + detallesNuevos.getZona().getId() + " no existe."));

            escenarioExistente.setNombre(detallesNuevos.getNombre());
            escenarioExistente.setPuertaAcceso(detallesNuevos.getPuertaAcceso());
            escenarioExistente.setAforoMaximo(detallesNuevos.getAforoMaximo());
            escenarioExistente.setPrecio(detallesNuevos.getPrecio()); 
          
            escenarioExistente.setZona(zonaReal); 
            
            return escenarioRepository.save(escenarioExistente);
        
    }

    public void eliminar(Long id) {
        if (!escenarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. El escenario con ID " + id + " no existe.");
        }
        escenarioRepository.deleteById(id);
    }
}