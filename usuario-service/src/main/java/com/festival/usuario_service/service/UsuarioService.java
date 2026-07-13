package com.festival.usuario_service.service;

import com.festival.usuario_service.exception.BadRequestException;
import com.festival.usuario_service.exception.ResourceNotFoundException;
import com.festival.usuario_service.model.Usuario;
import com.festival.usuario_service.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario guardar(Usuario usuario) {
        logger.info("Intentando guardar usuario con email={}", usuario.getEmail());
        
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            logger.warn("Validación fallida: El email {} ya está en uso", usuario.getEmail());
            throw new BadRequestException("El correo electrónico ya está en uso.");
        }

        if (usuario.getRut() != null && usuarioRepository.existsByRut(usuario.getRut())) {
            logger.warn("Validación fallida: El RUT {} ya está en uso", usuario.getRut());
             throw new BadRequestException("El RUT ingresado ya está asociado a otra cuenta.");
        }

        Usuario guardado = usuarioRepository.save(usuario);
        logger.info("Usuario guardado exitosamente id={}", guardado.getId());
        return guardado;
    }

    public List<Usuario> listarUsuarios() {
        logger.info("Listando todos los usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();
        logger.debug("Cantidad de usuarios encontrados: {}", usuarios.size());
        return usuarios;
    }

    public Usuario buscarPorId(Long id) {
        logger.info("Buscando usuario por ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Busqueda fallida: Usuario id={} no encontrado", id);
                    return new ResourceNotFoundException("El usuario con ID " + id + " no fue encontrado.");
                });
    }

    public boolean existePorId(Long id) {
        logger.debug("Verificando existencia de usuario id={}", id);
        return usuarioRepository.existsById(id);
    }

    public Usuario actualizarUsuario(Long id, String nuevoNombre, String nuevoEmail, String nuevoRut, LocalDate nuevaFecha) {
            logger.info("Iniciando actualización de usuario id={}", id);   
            Usuario usuario = buscarPorId(id);
                
            if (!usuario.getEmail().equals(nuevoEmail) && usuarioRepository.existsByEmail(nuevoEmail)) {
                logger.warn("Validación fallida: El nuevo email {} ya está en uso", nuevoEmail);
                throw new BadRequestException("El nuevo correo electrónico ya está en uso.");
            }

            if (nuevoRut != null && !nuevoRut.equals(usuario.getRut()) && usuarioRepository.existsByRut(nuevoRut)) {
                logger.warn("Validación fallida: El nuevo RUT {} ya está en uso", nuevoRut);
                throw new BadRequestException("El nuevo RUT ya está asociado a otra cuenta.");
            }

            usuario.setNombre(nuevoNombre);
            usuario.setEmail(nuevoEmail);
            usuario.setRut(nuevoRut);          
            usuario.setFechaNacimiento(nuevaFecha);

            Usuario actualizado = usuarioRepository.save(usuario);
            logger.info("Usuario id={} actualizado exitosamente", actualizado.getId());
            return actualizado;
        
    }

    public void eliminarUsuario(Long id) {
        logger.info("Intentando eliminar usuario id={}", id);
        if (!usuarioRepository.existsById(id)) {
            logger.warn("Eliminación fallida: Usuario id={} no existe", id);
            throw new ResourceNotFoundException("No se puede eliminar. El usuario con ID " + id + " no fue encontrado.");
        }
        usuarioRepository.deleteById(id);
        logger.info("Usuario id={} eliminado exitosamente", id);
    }
}