package com.festival.artista_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.festival.artista_service.model.Artista;


@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    // Devuelve SOLO los IDs de los artistas que coinciden con el género (ignorando mayúsculas/minúsculas)
    @Query("SELECT a.id FROM Artista a WHERE LOWER(a.generoMusical) = LOWER(:genero)")
    List<Long> findIdsByGeneroMusical(@Param("genero") String genero);
    
}
