package com.pstreaming.service;

import com.pstreaming.domain.Usuario;
import com.pstreaming.dto.*;
import com.pstreaming.repository.UsuarioRepository;
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
    private PasswordEncoder aEncoder;
    @Autowired
    private AuthService authService;
    @Autowired
    private TwoFAPolicyService twoFAserivice;

    @Transactional
    public UsuarioResponse save(UsuarioRegistroRequest rq) {
        Usuario u = new Usuario();

        if (existeByCorreo(rq.getCorreo())) {
            throw new RuntimeException("Este correo ya se encuentra registrado.");
        }

//        if (twoFAserivice.require2FA(u)) {
//            
//        }
        u.setNombre(rq.getNombre());
        u.setApellido_1(rq.getApellido_1());
        u.setCorreo(rq.getCorreo());
        u.setPassword(aEncoder.encode(rq.getPassword()));
        u.setTelefono(rq.getTelefono());
        u.setPalabraClave(rq.getPalabraClave());

        return null;
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

        return res;
    }

    private UsuarioLoginResponse toResponseLogin(Usuario u) {
        UsuarioLoginResponse res = new UsuarioLoginResponse();

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
    public Usuario getUsuatioByCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.findByCorreo(correo.trim().toLowerCase());
    }
}
