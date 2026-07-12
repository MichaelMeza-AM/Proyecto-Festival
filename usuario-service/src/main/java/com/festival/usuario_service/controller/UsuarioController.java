package com.festival.usuario_service.controller;

import com.festival.usuario_service.dto.UsuarioDTO;
import com.festival.usuario_service.exception.ForbiddenException;
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
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(@PathVariable Long id, Authentication auth) {
        
        // auth.getName() extrae el "Subject" del token.
        Long userIdToken = Long.parseLong(auth.getName()); 

        // Verifica si dentro del token viene el rol de administrador.
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));

        // 2. Bloqueamos si es un intruso 
        if (!isAdmin && !id.equals(userIdToken)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso sobre este perfil"); 
        }

        // 3. Si todo está en orden, devolvemos los datos
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(UsuarioDTO.fromModel(usuario));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> obtenerMiPerfil(Authentication auth) {
        Long miId = Long.parseLong(auth.getName());       
        Usuario miPerfil = usuarioService.buscarPorId(miId);
        return ResponseEntity.ok(UsuarioDTO.fromModel(miPerfil));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Usuario nuevo = usuarioService.guardar(usuarioDTO.toModel());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.fromModel(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id, 
            @Valid @RequestBody UsuarioDTO usuarioDTO, 
            Authentication auth) {

    
        Long userIdToken = Long.parseLong(auth.getName()); 
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !id.equals(userIdToken)) {    
            throw new ForbiddenException("Acceso denegado: No tienes permiso sobre este perfil");
        }

        Usuario actualizado = usuarioService.actualizarUsuario(
                id, 
                usuarioDTO.getNombre(), 
                usuarioDTO.getEmail(),
                usuarioDTO.getRut(),
                usuarioDTO.getFechaNacimiento()
        );

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
       usuarioService.eliminarUsuario(id);
       return ResponseEntity.noContent().build();
    }
}