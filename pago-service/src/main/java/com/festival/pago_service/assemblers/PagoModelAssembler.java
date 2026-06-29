package com.festival.pago_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.pago_service.controller.PagoControllerV2;
import com.festival.pago_service.dto.PagoResponseDTO;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<PagoResponseDTO, EntityModel<PagoResponseDTO>> {

    @Override
    public EntityModel<PagoResponseDTO> toModel(PagoResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(PagoControllerV2.class).obtenerPago(dto.getId())).withSelfRel(),
                linkTo(methodOn(PagoControllerV2.class).listarTodos()).withRel("pagos"));  
    }
}