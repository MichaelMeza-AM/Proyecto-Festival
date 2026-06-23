package com.festival.pago_service.repository;

import com.festival.pago_service.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    boolean existsByIdCompra(Long idCompra);
}