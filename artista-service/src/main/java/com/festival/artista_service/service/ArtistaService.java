package com.festival.artista_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

    public Optional<Artista> buscarPorId(Long id) {
        return artistaRepository.findById(id);
    }

    public boolean existePorId(Long id) {
        return artistaRepository.existsById(id);
    }

    public void eliminar(Long id) {
        artistaRepository.deleteById(id);
    }

    public Optional<Artista> actualizar(Long id, Artista detallesNuevos) {
        return artistaRepository.findById(id).map(artistaExistente -> {
            // Actualizamos los campos
            artistaExistente.setNombre(detallesNuevos.getNombre());
            artistaExistente.setBiografia(detallesNuevos.getBiografia());
            artistaExistente.setGeneroMusical(detallesNuevos.getGeneroMusical());
            // Guardamos el artista modificado
            return artistaRepository.save(artistaExistente);
        });
    }

    public List<Long> obtenerIdsPorGenero(String genero) {
        return artistaRepository.findIdsByGeneroMusical(genero);
    }
}
