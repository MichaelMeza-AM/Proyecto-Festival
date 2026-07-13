package com.festival.ticket_service.service;

import com.festival.ticket_service.model.Ticket;
import com.festival.ticket_service.repository.TicketRepository;
import com.festival.ticket_service.exception.ResourceNotFoundException; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // 1. EL NÚCLEO: Generar la entrada
    public Ticket guardar(Ticket ticket) {
        logger.info("Iniciando generación de ticket para la compraId={} y usuarioId={}", 
                    ticket.getCompraId(), ticket.getUsuarioId());

        // Generamos un código único comercial de 8 caracteres alfanuméricos. Ej: TICK-A1B2C3D4
        String codigoUnico = generarCodigoUnico();
        ticket.setCodigo(codigoUnico);

        Ticket guardado = ticketRepository.save(ticket);
        logger.info("Ticket generado exitosamente con ID={} y Código={}", guardado.getId(), guardado.getCodigo());
        
        return guardado;
    }

    // Método auxiliar para garantizar que el código nunca se repita
    private String generarCodigoUnico() {
        String codigo;
        do {
            // UUID genera códigos larguísimos, cortamos los primeros 8 caracteres
            String uuidCorto = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            codigo = "TICK-" + uuidCorto;
        } while (ticketRepository.findByCodigo(codigo).isPresent()); // Verifica en la BD que no exista ya
        
        return codigo;
    }

    // 2. BUSCADORES
    public List<Ticket> listarTodos() {
        logger.info("Listando todos los tickets del sistema");
        return ticketRepository.findAll();
    }

    public Ticket buscarPorId(Long id) {
        logger.info("Buscando ticket por ID={}", id);
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el ticket con ID: " + id));
    }

    public boolean existePorId(Long id) {
        return ticketRepository.existsById(id);
    }

    public Ticket buscarPorCodigo(String codigo) {
        logger.info("Buscando ticket por Código={}", codigo);
        return ticketRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún ticket con el código: " + codigo));
    }

    public List<Ticket> listarPorUsuario(Long usuarioId) {
        logger.info("Listando tickets del usuarioId={}", usuarioId);
        return ticketRepository.findByUsuarioId(usuarioId);
    }

    // 3. ACTUALIZAR (Similar a como lo hacen en CompraService)
    public Ticket actualizar(Long id, Ticket detallesNuevos) {
        logger.info("Iniciando actualización del ticket ID={}", id);

        Ticket ticketExistente = buscarPorId(id);

        // Actualizamos solo los datos que pueden cambiar (no tocamos ni el ID ni el Código ni el Escenario)
        ticketExistente.setFechaAsistencia(detallesNuevos.getFechaAsistencia());
        
        Ticket actualizado = ticketRepository.save(ticketExistente);
        logger.info("Ticket ID={} actualizado correctamente", actualizado.getId());
        
        return actualizado;
    }

    // 4. ELIMINAR
    public void eliminar(Long id) {
        logger.info("Intentando eliminar ticket ID={}", id);
        if (!ticketRepository.existsById(id)) {
            logger.warn("Cancelando: El ticket ID={} no existe", id);
            throw new ResourceNotFoundException("No se puede eliminar. El ticket con ID " + id + " no existe.");
        }
        ticketRepository.deleteById(id);
        logger.info("Ticket ID={} eliminado con éxito", id);
    }
}