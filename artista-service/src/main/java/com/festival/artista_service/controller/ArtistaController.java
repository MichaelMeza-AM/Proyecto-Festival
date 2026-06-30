package com.festival.artista_service.controller;

import com.festival.artista_service.dto.ArtistaDTO;
import com.festival.artista_service.model.Artista;
import com.festival.artista_service.service.ArtistaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @PostMapping
    public ResponseEntity<ArtistaDTO> crearArtista(@Valid @RequestBody ArtistaDTO artistaDto) {
        Artista nuevo = artistaService.guardar(artistaDto.toModel());
        return ResponseEntity.status(HttpStatus.CREATED).body(ArtistaDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<ArtistaDTO>> listarArtistas() {
        List<Artista> artistas = artistaService.listar();
        List<ArtistaDTO> dtos = artistas.stream()
                .map(ArtistaDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistaDTO> obtenerPorId(@PathVariable Long id) {
        Artista artista = artistaService.buscarPorId(id);
        return ResponseEntity.ok(ArtistaDTO.fromModel(artista));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existeArtista(@PathVariable Long id) {
        return ResponseEntity.ok(artistaService.existePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistaDTO> actualizarArtista(@PathVariable Long id, @Valid @RequestBody ArtistaDTO artistaDto) {
        Artista actualizado = artistaService.actualizar(id, artistaDto.toModel());
        return ResponseEntity.ok(ArtistaDTO.fromModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarArtista(@PathVariable Long id) {   
        artistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/genero/{genero}/ids")
    public ResponseEntity<List<Long>> obtenerIdsPorGenero(@PathVariable String genero) {
        List<Long> ids = artistaService.obtenerIdsPorGenero(genero);
        return ResponseEntity.ok(ids);
    }
}