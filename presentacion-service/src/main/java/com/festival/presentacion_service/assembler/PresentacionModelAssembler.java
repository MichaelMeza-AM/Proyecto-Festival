package com.festival.presentacion_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.presentacion_service.controller.PresentacionControllerV2;
import com.festival.presentacion_service.model.Presentacion;

@Component
public class PresentacionModelAssembler implements RepresentationModelAssembler<Presentacion, EntityModel<Presentacion>> {

    @Override
    public EntityModel<Presentacion> toModel(Presentacion presentacion) {
        return EntityModel.of(presentacion,
                linkTo(methodOn(PresentacionControllerV2.class).obtenerPorId(presentacion.getId())).withSelfRel(),
                linkTo(methodOn(PresentacionControllerV2.class).listar()).withRel("presentaciones"));  
    }
}