package com.festival.artista_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festival.artista_service.assembler.ArtistaModelAssembler;
import com.festival.artista_service.model.Artista;
import com.festival.artista_service.service.ArtistaService;

@RestController
@RequestMapping("/artistas/v2")
public class ArtistaControllerV2 {

    private final ArtistaService artistaService;
    private final ArtistaModelAssembler assembler;

    public ArtistaControllerV2(ArtistaService artistaService, ArtistaModelAssembler assembler) {
        this.artistaService = artistaService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Artista>> listarArtistas() {
        List<EntityModel<Artista>> artistas = artistaService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(artistas, 
                linkTo(methodOn(ArtistaControllerV2.class).listarArtistas()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Artista> obtenerPorId(@PathVariable Long id) {
        Artista artista = artistaService.buscarPorId(id);        
        return assembler.toModel(artista);
    }
}