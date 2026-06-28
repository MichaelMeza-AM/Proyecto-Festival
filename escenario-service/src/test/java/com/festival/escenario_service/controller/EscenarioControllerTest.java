package com.festival.escenario_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.festival.escenario_service.model.Escenario;
import com.festival.escenario_service.model.Zona;
import com.festival.escenario_service.dto.EscenarioDTO;
import com.festival.escenario_service.dto.ZonaDTO;
import com.festival.escenario_service.service.EscenarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EscenarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EscenarioService escenarioService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Escenario escenario;
    private EscenarioDTO escenarioDto;
    private Zona zona;
    private ZonaDTO zonaDto;

    @BeforeEach
    void setUp() {
        zona = new Zona(1L, "Explanada Central", "Principal");
        zonaDto = new ZonaDTO(1L, "Explanada Central", "Principal");

        escenario = new Escenario(1L, "Main Stage", "Puerta A", 40000, 65000, zona);
        escenarioDto = new EscenarioDTO(null, "Main Stage", "Puerta A", 40000, 65000, zonaDto);

        EscenarioController controller = new EscenarioController(escenarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testListar() throws Exception {
        when(escenarioService.listar()).thenReturn(List.of(escenario));

        mockMvc.perform(get("/escenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Main Stage"));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(escenarioService.buscarPorId(1L)).thenReturn(Optional.of(escenario));

        mockMvc.perform(get("/escenarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Main Stage"))
                .andExpect(jsonPath("$.zona.id").value(1L));
    }

    @Test
    public void testCrearEscenario() throws Exception {
        when(escenarioService.guardar(any(Escenario.class))).thenReturn(escenario);

        mockMvc.perform(post("/escenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(escenarioDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Main Stage"));
    }

    @Test
    public void testActualizarEscenario() throws Exception {
        when(escenarioService.actualizar(eq(1L), any(Escenario.class))).thenReturn(Optional.of(escenario));

        mockMvc.perform(put("/escenarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(escenarioDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testEliminarEscenario() throws Exception {
        when(escenarioService.existePorId(1L)).thenReturn(true);
        doNothing().when(escenarioService).eliminar(1L);

        mockMvc.perform(delete("/escenarios/1"))
                .andExpect(status().isNoContent());
        
        verify(escenarioService, times(1)).eliminar(1L);
    }
}