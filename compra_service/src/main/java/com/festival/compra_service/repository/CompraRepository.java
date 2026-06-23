package com.festival.compra_service.repository;

import com.festival.compra_service.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    
    // Nos servirá para que el usuario vea sus propias compras
    List<Compra> findByUsuarioId(Long usuarioId);
}