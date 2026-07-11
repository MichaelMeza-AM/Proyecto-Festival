package com.festival.ticket_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festival.ticket_service.assembler.TicketModelAssembler;
import com.festival.ticket_service.dto.TicketResponseDTO;
import com.festival.ticket_service.model.Ticket;
import com.festival.ticket_service.service.TicketService;

@RestController
@RequestMapping("/tickets/v2")
public class TicketControllerV2 {

    private final TicketService ticketService;
    private final TicketModelAssembler assembler;

    public TicketControllerV2(TicketService ticketService, TicketModelAssembler assembler) {
        this.ticketService = ticketService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<TicketResponseDTO>> listarTodos() {
        List<EntityModel<TicketResponseDTO>> tickets = ticketService.listarTodos().stream()
                .map(TicketResponseDTO::fromModel)
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(tickets, 
                linkTo(methodOn(TicketControllerV2.class).listarTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<TicketResponseDTO> obtenerPorId(@PathVariable Long id) {
        Ticket ticket = ticketService.buscarPorId(id);        
        TicketResponseDTO dto = TicketResponseDTO.fromModel(ticket);
        
        return assembler.toModel(dto);
    }
}