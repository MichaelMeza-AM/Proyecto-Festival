package com.festival.ticket_service.controller;

import com.festival.ticket_service.dto.TicketRequestDTO;
import com.festival.ticket_service.dto.TicketResponseDTO;
import com.festival.ticket_service.exception.ForbiddenException;
import com.festival.ticket_service.model.Ticket;
import com.festival.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> crearTicket(@Valid @RequestBody TicketRequestDTO request, Authentication auth) {
        Long usuarioId = Long.parseLong(auth.getName());
        
        Ticket nuevoTicket = new Ticket();
        nuevoTicket.setCompraId(request.getCompraId());
        nuevoTicket.setUsuarioId(usuarioId); 
        nuevoTicket.setEscenarioId(request.getEscenarioId());
        nuevoTicket.setFechaAsistencia(request.getFechaAsistencia());

        Ticket guardado = ticketService.guardar(nuevoTicket);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponseDTO.fromModel(guardado));
    }

    @GetMapping("/me")
    public ResponseEntity<List<TicketResponseDTO>> listarMisTickets(Authentication auth) {
        Long usuarioId = Long.parseLong(auth.getName());
        
        List<TicketResponseDTO> dtos = ticketService.listarPorUsuario(usuarioId).stream()
                .map(TicketResponseDTO::fromModel)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> listarTodos() {
        List<TicketResponseDTO> dtos = ticketService.listarTodos().stream()
                .map(TicketResponseDTO::fromModel)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> obtenerPorId(@PathVariable Long id, Authentication auth) {
        Ticket existente = ticketService.buscarPorId(id);
        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para ver este ticket");
        }

        return ResponseEntity.ok(TicketResponseDTO.fromModel(existente));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<TicketResponseDTO> obtenerPorCodigo(@PathVariable String codigo, Authentication auth) {
        Ticket existente = ticketService.buscarPorCodigo(codigo);

        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para ver este ticket");
        }

        return ResponseEntity.ok(TicketResponseDTO.fromModel(existente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TicketRequestDTO request, Authentication auth) {
        Ticket existente = ticketService.buscarPorId(id);

        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para modificar este ticket");
        }

        Ticket datosNuevos = new Ticket();
        datosNuevos.setEscenarioId(request.getEscenarioId());
        datosNuevos.setFechaAsistencia(request.getFechaAsistencia());

        Ticket actualizado = ticketService.actualizar(id, datosNuevos);

        return ResponseEntity.ok(TicketResponseDTO.fromModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        Ticket existente = ticketService.buscarPorId(id);

        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para eliminar este ticket");
        }

        ticketService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}