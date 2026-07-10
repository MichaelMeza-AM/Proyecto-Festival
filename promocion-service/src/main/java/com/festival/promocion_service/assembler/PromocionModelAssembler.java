package com.festival.promocion_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.promocion_service.controller.PromocionControllerV2;
import com.festival.promocion_service.dto.PromocionResponseDTO;

@Component
public class PromocionModelAssembler implements RepresentationModelAssembler<PromocionResponseDTO, EntityModel<PromocionResponseDTO>> {

    @Override
    public EntityModel<PromocionResponseDTO> toModel(PromocionResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(PromocionControllerV2.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(PromocionControllerV2.class).listarTodas()).withRel("promociones"));  
    }
}