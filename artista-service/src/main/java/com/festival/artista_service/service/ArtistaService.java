package com.festival.artista_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.festival.artista_service.exception.ResourceNotFoundException;
import com.festival.artista_service.model.Artista;
import com.festival.artista_service.repository.ArtistaRepository;


@Service
public class ArtistaService {
    
    private static final Logger logger = LoggerFactory.getLogger(ArtistaService.class);
    private final ArtistaRepository artistaRepository;

    public ArtistaService(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    public Artista guardar(Artista artista) {
       logger.info("Iniciando guardado de artista: {}", artista.getNombre());
        Artista guardado = artistaRepository.save(artista);
        logger.info("Artista guardado exitosamente id={}", guardado.getId());
        return guardado;
    }

    public List<Artista> listar() {
        logger.info("Listando todos los artistas");
        List<Artista> artistas = artistaRepository.findAll();
        logger.debug("Cantidad de artistas encontrados: {}", artistas.size());
        return artistas;
    }

    public Artista buscarPorId(Long id) {
        logger.info("Buscando artista por id={}", id);
        return artistaRepository.findById(id)
         .orElseThrow(() -> {
             logger.warn("Busqueda fallida: Artista id={} no encontrado", id);
             return new ResourceNotFoundException("Artista no encontrado con id: " + id);
         });
    }

    public boolean existePorId(Long id) {
        logger.debug("Verificando existencia de artista id={}", id);
        return artistaRepository.existsById(id);
    }

    public void eliminar(Long id) {
        logger.info("Intentando eliminar artista id={}", id);
        if (!artistaRepository.existsById(id)) {
            logger.warn("Eliminación fallida: Artista id={} no existe", id);
            throw new ResourceNotFoundException("No se puede eliminar. El artista  con id: " + id + " no existe.");
        }
        artistaRepository.deleteById(id);
        logger.info("Artista id={} eliminado exitosamente", id);
    }

    public Artista actualizar(Long id, Artista detallesNuevos) {
        logger.info("Iniciando actualización de artista id={}", id);
        Artista artistaExistente = buscarPorId(id);
        
            artistaExistente.setNombre(detallesNuevos.getNombre());
            artistaExistente.setBiografia(detallesNuevos.getBiografia());
            artistaExistente.setGeneroMusical(detallesNuevos.getGeneroMusical());

            return artistaRepository.save(artistaExistente);
        
    }

    public List<Long> obtenerIdsPorGenero(String genero) {
        logger.info("Buscando IDs de artistas para el género: {}", genero);
        List<Long> ids = artistaRepository.findIdsByGeneroMusical(genero);
        logger.debug("Cantidad de IDs encontrados para el género {}: {}", genero, ids.size());
        return ids;
    }
}
