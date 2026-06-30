package com.festival.presentacion_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festival.presentacion_service.assembler.PresentacionModelAssembler;
import com.festival.presentacion_service.model.Presentacion;
import com.festival.presentacion_service.service.PresentacionService;

@RestController
@RequestMapping("/presentaciones/v2")
public class PresentacionControllerV2 {

    private final PresentacionService presentacionService;
    private final PresentacionModelAssembler assembler;

    public PresentacionControllerV2(PresentacionService presentacionService, PresentacionModelAssembler assembler) {
        this.presentacionService = presentacionService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Presentacion>> listar() {
        List<EntityModel<Presentacion>> presentaciones = presentacionService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(presentaciones, 
                linkTo(methodOn(PresentacionControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Presentacion> obtenerPorId(@PathVariable Long id) {
        Presentacion presentacion = presentacionService.buscarPorId(id);
        return assembler.toModel(presentacion);
    }
}