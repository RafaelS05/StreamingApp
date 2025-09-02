package com.pstreaming.service;

import com.pstreaming.domain.Usuario;
import com.pstreaming.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Usuario getUsuarioByCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.findByCorreo(correo.trim().toLowerCase());
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return usuarioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existeByCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.existsByCorreo(correo.trim().toLowerCase());
    }
    
    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        
        if (usuario.getCorreo() != null) {
            usuario.setCorreo(usuario.getCorreo().trim().toLowerCase());
        }
        
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public boolean delete(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        
        try {
            usuarioRepository.delete(usuario);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            if (!usuarioRepository.existsById(id)) {
                return false;
            }
            
            usuarioRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario por ID: " + e.getMessage());
            return false;
        }
    }
    @Transactional
    public Usuario actualizar(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("El usuario y su ID no pueden ser null");
        }
        
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario());
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuario.getIdUsuario());
        }
        
        if (usuario.getCorreo() != null) {
            usuario.setCorreo(usuario.getCorreo().trim().toLowerCase());
        }
        
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public long contarUsuarios() {
        return usuarioRepository.count();
    }
}