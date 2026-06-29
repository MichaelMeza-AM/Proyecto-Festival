package com.festival.compra_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.compra_service.controller.CompraControllerV2;
import com.festival.compra_service.dto.CompraResponseDTO;

@Component
public class CompraModelAssembler implements RepresentationModelAssembler<CompraResponseDTO, EntityModel<CompraResponseDTO>> {

    @Override
    public EntityModel<CompraResponseDTO> toModel(CompraResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(CompraControllerV2.class).obtenerCompra(dto.getId())).withSelfRel(),
                linkTo(methodOn(CompraControllerV2.class).listarTodos()).withRel("compras"));  
    }
}