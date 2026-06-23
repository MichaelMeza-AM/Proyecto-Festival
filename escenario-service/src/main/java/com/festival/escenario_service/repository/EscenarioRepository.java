package com.festival.escenario_service.repository;

import com.festival.escenario_service.model.Escenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscenarioRepository extends JpaRepository<Escenario, Long> {
}