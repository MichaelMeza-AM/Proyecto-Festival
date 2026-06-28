package com.festival.usuario_service.controller;

import com.festival.usuario_service.dto.UsuarioDTO;
import com.festival.usuario_service.exception.ForbiddenException;
import com.festival.usuario_service.exception.ResourceNotFoundException;
import com.festival.usuario_service.model.Usuario;
import com.festival.usuario_service.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.existePorId(id));
    }
    
    @GetMapping  
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios()
                .stream()
                .map(UsuarioDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id, Authentication auth) {
        
        // auth.getName() extrae el "Subject" del token.
        // Aquí lo recuperamos mágicamente sin ir a la base de datos.
        Long userIdToken = Long.parseLong(auth.getName()); 

        // Verifica si dentro del token viene el rol de administrador.
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));

        // 2. Bloqueamos si es un intruso husmeando perfiles ajenos
        if (!isAdmin && !id.equals(userIdToken)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso sobre este perfil"); 
        }

        // 3. Si todo está en orden, devolvemos los datos
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID " + id + " no existe"));
        
        return ResponseEntity.ok(UsuarioDTO.fromModel(usuario));
    }

  @GetMapping("/me") //devuelve el perfil del usuario autenticado
    public ResponseEntity<UsuarioDTO> obtenerMiPerfil(Authentication auth) {
        // auth.getName() ahora nos entrega directamente el ID (Ej: "2")
        Long miId = Long.parseLong(auth.getName()); 
        
        // Buscamos súper rápido por ID en lugar de por email
        Usuario miPerfil = usuarioService.buscarPorId(miId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un perfil asociado a tu cuenta."));
        
        return ResponseEntity.ok(UsuarioDTO.fromModel(miPerfil));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crearPerfil(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Usuario nuevo = usuarioService.guardar(usuarioDTO.toModel());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.fromModel(nuevo));
    }

    @PutMapping("/{id}") // ACTUALIZAR PERFIL (Con validación de identidad)
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id, 
            @Valid @RequestBody UsuarioDTO usuarioDTO, 
            Authentication auth) {

        Usuario usuarioExistente = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID " + id + " no existe"));

        Long userIdToken = Long.parseLong(auth.getName()); 
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !usuarioExistente.getId().equals(userIdToken)) {    // SEGURIDAD: Solo el dueño del perfil o un ADMIN pueden editar
            throw new ForbiddenException("Acceso denegado: No tienes permiso sobre este perfil");
        }

        Usuario actualizado = usuarioService.actualizarUsuario(
                id, 
                usuarioDTO.getNombre(), 
                usuarioDTO.getEmail(),
                usuarioDTO.getRut(),
                usuarioDTO.getFechaNacimiento()
        ).orElseThrow(() -> new ResourceNotFoundException("Error al actualizar: Usuario no encontrado"));

        return ResponseEntity.ok(UsuarioDTO.fromModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id, Authentication auth) {
        
        // 1. Extraemos quién está haciendo la petición
        Long userIdToken = Long.parseLong(auth.getName()); 
        
        // 2. Verificamos si es administrador
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));

        // 3. SEGURIDAD: Si no es Admin, y el ID que intenta borrar no es el suyo, lo bloqueamos.
        if (!isAdmin && !id.equals(userIdToken)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso sobre este perfil");
        }

        // 4. Si pasa la seguridad, procedemos a borrar
        if (!usuarioService.eliminarUsuario(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Usuario no encontrado");
        }
        
        return ResponseEntity.noContent().build();
    }
}