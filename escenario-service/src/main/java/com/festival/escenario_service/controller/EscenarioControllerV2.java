package com.festival.escenario_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festival.escenario_service.assembler.EscenarioModelAssembler;
import com.festival.escenario_service.model.Escenario;
import com.festival.escenario_service.service.EscenarioService;

@RestController
@RequestMapping("/escenarios/v2")
public class EscenarioControllerV2 {

    private final EscenarioService escenarioService;
    private final EscenarioModelAssembler assembler;

    public EscenarioControllerV2(EscenarioService escenarioService, EscenarioModelAssembler assembler) {
        this.escenarioService = escenarioService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Escenario>> listar() {
        List<EntityModel<Escenario>> escenarios = escenarioService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(escenarios, 
                linkTo(methodOn(EscenarioControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Escenario> obtenerPorId(@PathVariable Long id) {
        Escenario escenario = escenarioService.buscarPorId(id);
        return assembler.toModel(escenario);
    }
}