package com.festival.pago_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.festival.pago_service.dto.PagoRequestDTO;
import com.festival.pago_service.exception.BadRequestException;
import com.festival.pago_service.exception.ResourceNotFoundException;
import com.festival.pago_service.model.Pago;
import com.festival.pago_service.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    private PagoService pagoService;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @BeforeEach
    void setUp() throws Exception {
        // Inicializamos el servicio simulando el WebClient como en el ejemplo del profe
        when(webClientBuilder.build()).thenReturn(webClient);
        pagoService = new PagoService(pagoRepository, webClientBuilder);

        // Usamos Reflection para inyectar las URL simuladas
        Field compraUrlField = PagoService.class.getDeclaredField("compraUrl");
        compraUrlField.setAccessible(true);
        compraUrlField.set(pagoService, "http://api/compra");

        Field escenarioUrlField = PagoService.class.getDeclaredField("escenarioUrl");
        escenarioUrlField.setAccessible(true);
        escenarioUrlField.set(pagoService, "http://api/escenario");
    }


    @Test
    void testListarTodos() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setMontoTotal(5950);
        when(pagoRepository.findAll()).thenReturn(List.of(pago));

        List<Pago> pagos = pagoService.listarTodos();

        assertNotNull(pagos);
        assertEquals(1, pagos.size());
        verify(pagoRepository).findAll();
    }

    @Test
    void testObtenerPorId() {
        Long id = 1L;
        Pago pago = new Pago();
        pago.setId(id);
        pago.setMontoTotal(1090);

        when(pagoRepository.findById(id)).thenReturn(Optional.of(pago));

        Pago resultado = pagoService.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(pagoRepository).findById(id);
    }

    @Test
    void testActualizar() {
        Long id = 1L;
        Pago existente = new Pago();
        existente.setId(id);
        existente.setMedioPago("Efectivo");

        PagoRequestDTO request = new PagoRequestDTO();
        request.setMedioPago("Tarjeta");

        when(pagoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(pagoRepository.save(any(Pago.class))).thenReturn(existente);

        Pago resultado = pagoService.actualizar(id, request);

        assertNotNull(resultado);
        verify(pagoRepository).findById(id);
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void testEliminar() {
        Long id = 1L;
        when(pagoRepository.existsById(id)).thenReturn(true);
        
        pagoService.eliminar(id);
        
        verify(pagoRepository).deleteById(id);
    }

    // PRUEBAS DE MÉTODOS MATEMÁTICOS (Sí profe, se lo copiamos a usted)

    @Test
    void testCalcularMontoDescuento() {
        int descuento = pagoService.calcularMontoDescuento(1000, 10); // 10% de 1000
        assertEquals(100, descuento);

        assertThrows(BadRequestException.class, () -> pagoService.calcularMontoDescuento(-1, 10));
        assertThrows(BadRequestException.class, () -> pagoService.calcularMontoDescuento(1000, 105));
    }

    @Test
    void testCalcularSubtotalConDescuento() {
        int subtotal = pagoService.calcularSubtotalConDescuento(1000, 100);
        assertEquals(900, subtotal);
    }

    @Test
    void testCalcularIVA() {
        int iva = pagoService.calcularIVA(1000); // 19% de 1000
        assertEquals(190, iva);

        assertThrows(BadRequestException.class, () -> pagoService.calcularIVA(-50));
    }

    @Test
    void testCalcularTotal() {
        int total = pagoService.calcularTotal(1000, 190);
        assertEquals(1190, total);
    }
}