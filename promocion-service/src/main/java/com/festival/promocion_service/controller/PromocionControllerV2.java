package com.festival.promocion_service.controller;

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

import com.festival.promocion_service.assembler.PromocionModelAssembler;
import com.festival.promocion_service.dto.PromocionResponseDTO;
import com.festival.promocion_service.model.Promocion;
import com.festival.promocion_service.service.PromocionService;

@RestController
@RequestMapping("/promociones/v2")
public class PromocionControllerV2 {

    private final PromocionService promocionService;
    private final PromocionModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(PromocionControllerV2.class);

    public PromocionControllerV2(PromocionService promocionService, PromocionModelAssembler assembler) {
        this.promocionService = promocionService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<PromocionResponseDTO>> listarTodas() {
        logger.info("V2 GET /promociones/v2 - Listando promociones con soporte HATEOAS");

        List<EntityModel<PromocionResponseDTO>> promociones = promocionService.listarTodas().stream()
                .map(PromocionResponseDTO::fromModel)
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(promociones, 
                linkTo(methodOn(PromocionControllerV2.class).listarTodas()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<PromocionResponseDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("V2 GET /promociones/v2/{} - Recuperando información de promoción individual", id);
        
        Promocion promocion = promocionService.buscarPorId(id);        
        PromocionResponseDTO dto = PromocionResponseDTO.fromModel(promocion);
        
        return assembler.toModel(dto);
    }
}