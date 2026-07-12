package com.festival.artista_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.festival.artista_service.exception.ResourceNotFoundException;
import com.festival.artista_service.model.Artista;
import com.festival.artista_service.repository.ArtistaRepository;


@Service
public class ArtistaService {
    
    private final ArtistaRepository artistaRepository;

    public ArtistaService(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    public Artista guardar(Artista artista) {
        return artistaRepository.save(artista);
    }

    public List<Artista> listar() {
        return artistaRepository.findAll();
    }

    public Artista buscarPorId(Long id) {
        return artistaRepository.findById(id)
         .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con id: " + id));
    }

    public boolean existePorId(Long id) {
        return artistaRepository.existsById(id);
    }

    public void eliminar(Long id) {
        if (!artistaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. El artista  con id: " + id + " no existe.");
        }
        artistaRepository.deleteById(id);
    }

    public Artista actualizar(Long id, Artista detallesNuevos) {
        Artista artistaExistente = buscarPorId(id);
        
            artistaExistente.setNombre(detallesNuevos.getNombre());
            artistaExistente.setBiografia(detallesNuevos.getBiografia());
            artistaExistente.setGeneroMusical(detallesNuevos.getGeneroMusical());

            return artistaRepository.save(artistaExistente);
        
    }

    public List<Long> obtenerIdsPorGenero(String genero) {
        return artistaRepository.findIdsByGeneroMusical(genero);
    }
}
