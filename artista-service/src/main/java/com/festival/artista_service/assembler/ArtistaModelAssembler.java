package com.festival.artista_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.artista_service.controller.ArtistaControllerV2;
import com.festival.artista_service.model.Artista;

@Component
public class ArtistaModelAssembler implements RepresentationModelAssembler<Artista, EntityModel<Artista>> {

    @Override
    public EntityModel<Artista> toModel(Artista artista) {
        return EntityModel.of(artista,
                linkTo(methodOn(ArtistaControllerV2.class).obtenerPorId(artista.getId())).withSelfRel(),
                linkTo(methodOn(ArtistaControllerV2.class).listarArtistas()).withRel("artistas"));  
    }
}