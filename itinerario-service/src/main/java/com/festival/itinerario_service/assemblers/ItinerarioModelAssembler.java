package com.festival.itinerario_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.itinerario_service.controller.ItinerarioControllerV2;
import com.festival.itinerario_service.dto.ItinerarioResponseDTO;

@Component
public class ItinerarioModelAssembler implements RepresentationModelAssembler<ItinerarioResponseDTO, EntityModel<ItinerarioResponseDTO>> {

    @Override
    public EntityModel<ItinerarioResponseDTO> toModel(ItinerarioResponseDTO dto) {
        return EntityModel.of(dto,
                // Enlace "self": apunta al método obtenerItinerario de tu V2 usando el ID del DTO
                linkTo(methodOn(ItinerarioControllerV2.class).obtenerItinerario(dto.getId())).withSelfRel(),
                // Enlace a la colección: apunta al método que lista todos los itinerarios en tu V2
                linkTo(methodOn(ItinerarioControllerV2.class).listarTodos()).withRel("itinerarios"));  
    }
}