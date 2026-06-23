package com.festival.usuario_service.service;

import com.festival.usuario_service.exception.BadRequestException;
import com.festival.usuario_service.model.Usuario;
import com.festival.usuario_service.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // CREAR o GUARDAR con validación previa
    public Usuario guardar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BadRequestException("El correo electrónico ya está en uso.");
        }

        if (usuario.getRut() != null && usuarioRepository.existsByRut(usuario.getRut())) {
             throw new BadRequestException("El RUT ingresado ya está asociado a otra cuenta.");
        }

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }

    // ACTUALIZAR (Perfil completo del asistente)
    public Optional<Usuario> actualizarUsuario(Long id, String nuevoNombre, String nuevoEmail, String nuevoRut, LocalDate nuevaFecha) {
        return usuarioRepository.findById(id).map(usuario -> {
            
            // Validar si quiere cambiar el email, que el nuevo no esté usado por OTRO usuario
            if (!usuario.getEmail().equals(nuevoEmail) && usuarioRepository.existsByEmail(nuevoEmail)) {
                throw new BadRequestException("El nuevo correo electrónico ya está en uso.");
            }

            // Validar si quiere cambiar el RUT, que el nuevo no esté usado por OTRO usuario
            if (nuevoRut != null && !nuevoRut.equals(usuario.getRut()) && usuarioRepository.existsByRut(nuevoRut)) {
                 throw new BadRequestException("El nuevo RUT ya está asociado a otra cuenta.");
            }

            usuario.setNombre(nuevoNombre);
            usuario.setEmail(nuevoEmail);
            usuario.setRut(nuevoRut);          
            usuario.setFechaNacimiento(nuevaFecha);
            
            return usuarioRepository.save(usuario);
        });
    }

    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}