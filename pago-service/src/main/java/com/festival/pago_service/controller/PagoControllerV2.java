package com.festival.pago_service.controller;

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

import com.festival.pago_service.assemblers.PagoModelAssembler;
import com.festival.pago_service.dto.PagoResponseDTO;
import com.festival.pago_service.model.Pago;
import com.festival.pago_service.service.PagoService;

@RestController
@RequestMapping("/pagos/v2")
public class PagoControllerV2 {

    private final PagoService pagoService;
    private final PagoModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(PagoControllerV2.class);

    public PagoControllerV2(PagoService pagoService, PagoModelAssembler assembler) {
        this.pagoService = pagoService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<PagoResponseDTO>> listarTodos() {
        logger.info("V2 GET /pagos/v2 - Listando pagos con HATEOAS");

        List<EntityModel<PagoResponseDTO>> pagos = pagoService.listarTodos().stream()
                .map(PagoResponseDTO::fromModel)
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        return CollectionModel.of(pagos, linkTo(methodOn(PagoControllerV2.class).listarTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<PagoResponseDTO> obtenerPago(@PathVariable Long id) {
        logger.info("V2 GET /pagos/v2/{} - Obteniendo pago individual", id);
        
        Pago pago = pagoService.buscarPorId(id); 
                
        PagoResponseDTO dto = PagoResponseDTO.fromModel(pago); 
        
        return assembler.toModel(dto);
    }
}