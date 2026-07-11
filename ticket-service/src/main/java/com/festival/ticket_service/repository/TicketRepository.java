package com.festival.ticket_service.repository;

import com.festival.ticket_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByCodigo(String codigo);

    List<Ticket> findByUsuarioId(Long usuarioId);

    List<Ticket> findByCompraId(Long compraId);
}