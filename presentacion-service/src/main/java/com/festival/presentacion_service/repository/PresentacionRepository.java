package com.festival.presentacion_service.repository;

import com.festival.presentacion_service.model.Presentacion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresentacionRepository extends JpaRepository<Presentacion, Long> {

    // Busca todas las presentaciones cuyos artistas estén en la lista de IDs recibida
    List<Presentacion> findAllByArtistaIdIn(List<Long> artistaIds);

    // Spring Boot traduce esto automáticamente a: SELECT * FROM presentacion WHERE fecha_hora BETWEEN inicio AND fin
    List<Presentacion> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}