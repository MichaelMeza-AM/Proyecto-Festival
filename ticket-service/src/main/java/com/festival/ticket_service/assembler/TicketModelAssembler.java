package com.festival.ticket_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.ticket_service.controller.TicketControllerV2;
import com.festival.ticket_service.dto.TicketResponseDTO;

@Component
public class TicketModelAssembler implements RepresentationModelAssembler<TicketResponseDTO, EntityModel<TicketResponseDTO>> {

    @Override
    public EntityModel<TicketResponseDTO> toModel(TicketResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(TicketControllerV2.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(TicketControllerV2.class).listarTodos()).withRel("tickets"));  
    }
}