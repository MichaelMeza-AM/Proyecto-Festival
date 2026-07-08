package com.festival.pago_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.festival.pago_service.dto.PagoRequestDTO;
import com.festival.pago_service.model.Pago;
import com.festival.pago_service.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;


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