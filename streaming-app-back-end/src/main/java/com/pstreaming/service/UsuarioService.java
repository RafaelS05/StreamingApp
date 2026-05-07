package com.pstreaming.service;

import com.pstreaming.domain.*;
import com.pstreaming.dto.*;
import com.pstreaming.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EstadoRepository estadoRepository;
    @Autowired
    private PasswordEncoder aEncoder;
//    @Autowired
//    private AuthService authService;
//    @Autowired
//    private TwoFAPolicyService twoFAserivice;

    @Transactional
    public UsuarioResponse save(UsuarioRegistroRequest request) {

        if (existeByCorreo(request.getCorreo())) {
            throw new RuntimeException("Este correo ya se encuentra registrado.");
        }
        Usuario usuario = new Usuario();
        Estado estado = estadoRepository.findByNombre("ACTIVO");
        if (estado == null) {
            throw new RuntimeException("Este usuario no cuenta con un estado definido.");
        }
        usuario.setEstado(estado);

        usuario.setNombre(request.getNombre());
        usuario.setApellido_1(request.getApellido_1());
        usuario.setCorreo(request.getCorreo());
        usuario.setPassword(aEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setPalabraClave(request.getPalabraClave());
        usuarioRepository.save(usuario);

        return toResponse(usuario);
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

    /* Utilities */
    private UsuarioResponse toResponse(Usuario u) {
        UsuarioResponse res = new UsuarioResponse();
        res.setIdUsuario(u.getIdUsuario());
        res.setNombre(u.getNombre());
        res.setApellido_1(u.getApellido_1());
        res.setCorreo(u.getCorreo());
        res.setTelefono(u.getTelefono());
        res.setEstado(u.getEstado().getNombre());

        return res;
    }

    @Transactional(readOnly = true)
    public boolean existeByCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.existsByCorreo(correo.trim().toLowerCase());
    }

    @Transactional
    public Usuario getUsuarioByCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.findByCorreo(correo.trim().toLowerCase());
    }
    
    @Transactional
    public String getRol(Usuario usuario){
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            return "USER";
        }
        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .filter(nombre -> "ADMIN".equals(nombre))
                .findFirst()
                .orElse("USER");
    }
}
