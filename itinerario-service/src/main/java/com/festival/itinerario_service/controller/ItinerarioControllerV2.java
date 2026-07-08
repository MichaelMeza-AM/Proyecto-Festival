package com.festival.itinerario_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festival.itinerario_service.assemblers.ItinerarioModelAssembler;
import com.festival.itinerario_service.dto.ItinerarioResponseDTO;
import com.festival.itinerario_service.model.Itinerario;
import com.festival.itinerario_service.service.ItinerarioService;

@RestController
@RequestMapping("/itinerarios/v2")
public class ItinerarioControllerV2 {

    private final ItinerarioService itinerarioService;
    private final ItinerarioModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ItinerarioControllerV2.class);

    public ItinerarioControllerV2(ItinerarioService itinerarioService, ItinerarioModelAssembler assembler) {
        this.itinerarioService = itinerarioService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ItinerarioResponseDTO>> listarTodos() {
        logger.info("V2 GET /itinerarios/v2 - Listando itinerarios con HATEOAS");
        
        List<EntityModel<ItinerarioResponseDTO>> itinerarios = itinerarioService.listarTodos().stream()
                .map(itinerarioService::obtenerDetalleEnriquecido)
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        return CollectionModel.of(itinerarios, linkTo(methodOn(ItinerarioControllerV2.class).listarTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<ItinerarioResponseDTO> obtenerItinerario(@PathVariable Long id) {
        logger.info("V2 GET /itinerarios/v2/{} - Obteniendo itinerario individual", id);
        
        Itinerario itinerario = itinerarioService.buscarPorId(id);        
        ItinerarioResponseDTO dto = itinerarioService.obtenerDetalleEnriquecido(itinerario);
        
        return assembler.toModel(dto);
    }
}