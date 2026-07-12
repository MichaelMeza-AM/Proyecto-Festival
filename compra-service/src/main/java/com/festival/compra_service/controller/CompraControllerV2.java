package com.festival.compra_service.controller;

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

import com.festival.compra_service.assemblers.CompraModelAssembler;
import com.festival.compra_service.dto.CompraResponseDTO;
import com.festival.compra_service.model.Compra;
import com.festival.compra_service.service.CompraService;

@RestController
@RequestMapping("/compras/v2")
public class CompraControllerV2 {

    private final CompraService compraService;
    private final CompraModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(CompraControllerV2.class);

    public CompraControllerV2(CompraService compraService, CompraModelAssembler assembler) {
        this.compraService = compraService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<CompraResponseDTO>> listarTodos() {
        logger.info("V2 GET /compras/v2 - Listando compras con HATEOAS");
        
        List<EntityModel<CompraResponseDTO>> compras = compraService.listarTodos().stream()
                .map(CompraResponseDTO::fromModel)
                .map(dto -> assembler.toModel(dto))
                .collect(Collectors.toList());
                
        return CollectionModel.of(compras, linkTo(methodOn(CompraControllerV2.class).listarTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<CompraResponseDTO> obtenerCompra(@PathVariable Long id) {
        logger.info("V2 GET /compras/v2/{} - Obteniendo compra individual", id);
        
        Compra compra = compraService.buscarPorId(id);
                
        CompraResponseDTO dto = CompraResponseDTO.fromModel(compra);
        
        return assembler.toModel(dto);
    }
}