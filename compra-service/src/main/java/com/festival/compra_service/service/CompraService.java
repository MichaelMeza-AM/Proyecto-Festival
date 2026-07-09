package com.festival.compra_service.service;

import com.festival.compra_service.dto.CompraRequestDTO;
import com.festival.compra_service.model.Compra;
import com.festival.compra_service.repository.CompraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.festival.compra_service.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);
    private final CompraRepository compraRepository;

    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public Compra guardarCompra(Long usuarioId, CompraRequestDTO request) {
        logger.info("Registrando nueva intención de compra: usuarioId={}, escenarioId={}", usuarioId, request.getEscenarioId());

        Compra nuevaCompra = new Compra();
        nuevaCompra.setUsuarioId(usuarioId);
        nuevaCompra.setEscenarioId(request.getEscenarioId());
        nuevaCompra.setCantidad(request.getCantidad());
        nuevaCompra.setFechaAsistencia(request.getFechaAsistencia());
        nuevaCompra.setFechaCompra(LocalDateTime.now());

        return compraRepository.save(nuevaCompra);
    }

    public List<Compra> listarComprasPorUsuario(Long usuarioId) {
        return compraRepository.findByUsuarioId(usuarioId);
    }

    public List<Compra> listarTodos() {
        return compraRepository.findAll();
    }

    public Compra buscarPorId(Long id) {
        return compraRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No se encontró la compra con ID: " + id));
    }

    public Compra actualizar(Long id, CompraRequestDTO detallesNuevos) {
        logger.info("Actualizando registro de compra ID: {}", id);

        Compra compraExistente = buscarPorId(id); 

        compraExistente.setEscenarioId(detallesNuevos.getEscenarioId());
        compraExistente.setCantidad(detallesNuevos.getCantidad());
        compraExistente.setFechaAsistencia(detallesNuevos.getFechaAsistencia());
        compraExistente.setFechaCompra(LocalDateTime.now());
        
        return compraRepository.save(compraExistente);
    }

    public void eliminar(Long id) {
        if (!compraRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la compra con ID " + id);
        }
        compraRepository.deleteById(id);
        logger.info("Compra ID: {} eliminada", id);
    }
}