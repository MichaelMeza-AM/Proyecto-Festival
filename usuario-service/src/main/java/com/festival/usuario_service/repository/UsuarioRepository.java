package com.festival.usuario_service.repository;

import com.festival.usuario_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    boolean existsByEmail(String email);
    boolean existsByRut(String rut);
}