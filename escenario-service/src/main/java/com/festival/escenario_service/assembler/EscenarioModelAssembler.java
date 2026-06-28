package com.festival.escenario_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.escenario_service.controller.EscenarioControllerV2;
import com.festival.escenario_service.model.Escenario;

@Component
public class EscenarioModelAssembler implements RepresentationModelAssembler<Escenario, EntityModel<Escenario>> {

    @Override
    public EntityModel<Escenario> toModel(Escenario escenario) {
        return EntityModel.of(escenario,
                linkTo(methodOn(EscenarioControllerV2.class).obtenerPorId(escenario.getId())).withSelfRel(),
                linkTo(methodOn(EscenarioControllerV2.class).listar()).withRel("escenarios"));  
    }
}