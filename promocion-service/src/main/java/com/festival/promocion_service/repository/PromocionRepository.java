package com.festival.promocion_service.repository;

import com.festival.promocion_service.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    // Este método es clave para buscar la promoción cuando Pago Service nos envíe el código
    Optional<Promocion> findByCodigo(String codigo);
}