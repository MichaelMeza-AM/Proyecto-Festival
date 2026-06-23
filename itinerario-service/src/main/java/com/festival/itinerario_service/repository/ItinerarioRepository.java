package com.festival.itinerario_service.repository;

import com.festival.itinerario_service.model.Itinerario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {
    List<Itinerario> findByUsuarioId(Long usuarioId);
    
    //Devuelve true si ya existe esa combinación exacta
    boolean existsByUsuarioIdAndPresentacionId(Long usuarioId, Long presentacionId);
}