package com.pstreaming.service;

import com.pstreaming.domain.*;
import com.pstreaming.dto.*;
import com.pstreaming.repository.*;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository usuarioRepository;
    @Autowired
    private StatusRepository estadoRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private AuthMethodRepository metodoAuthRepository;
    @Autowired
    private PasswordEncoder aEncoder;

    @Transactional
    public UserResponse save(UserRegisterRequest request) {

        if (existeByCorreo(request.getCorreo())) {
            throw new RuntimeException("Este correo ya se encuentra registrado.");
        }
        User user = new User();
        Status status = estadoRepository.findByNombre("ACTIVO");
        if (status == null) {
            throw new RuntimeException("Este usuario no cuenta con un estado definido.");
        }
        user.setStatus(status);

        Rol rol = rolRepository.findByNombre("USER");

        user.setName(request.getNombre());
        user.setSurname(request.getApellido_1());
        user.setEmail(request.getCorreo());
        user.setPassword(aEncoder.encode(request.getPassword()));
        user.setPhone(request.getTelefono());

        AuthMethod metodo = metodoAuthRepository.findById(request.getMetodoAuth())
                .orElseThrow(() -> new RuntimeException("Metodo Invalido"));

        user.setAuthMethod(metodo);
        user.setRol(rol);
        user.setRegisterDate(LocalDateTime.now());
        usuarioRepository.save(user);

        return toResponse(user);
    }

    @Transactional
    public User actualizar(User user) {
        if (user == null || user.getIdUsuario() == null) {
            throw new IllegalArgumentException("El usuario y su ID no pueden ser null");
        }

        Optional<User> usuarioExistente = usuarioRepository.findById(user.getIdUsuario());
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + user.getIdUsuario());
        }

        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }

        return usuarioRepository.save(user);
    }

    @Transactional
    public boolean delete(User user) {
        if (user == null) {
            return false;
        }

        try {
            usuarioRepository.delete(user);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    /* Utilities */
    private UserResponse toResponse(User u) {
        UserResponse res = new UserResponse();
        res.setIdUsuario(u.getIdUsuario());
        res.setNombre(u.getName());
        res.setApellido_1(u.getSurname());
        res.setCorreo(u.getEmail());
        res.setTelefono(u.getPhone());
        res.setEstado(u.getStatus().getName());
        res.setMetodoAuth(u.getAuthMethod().getIdMethod());

        return res;
    }

    @Transactional(readOnly = true)
    public boolean existeByCorreo(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.existsByCorreo(email.trim().toLowerCase());
    }

    @Transactional
    public User getUsuarioByCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.findByCorreo(correo.trim().toLowerCase());
    }

    @Transactional
    public User findById(String idUsuario) {
        return usuarioRepository.findById(idUsuario).orElse(null);

    }

    @Transactional
    public String getRol(User user) {
        if (user.getRol() == null) {
            return "USER";
        }
        return user.getRol().getName();
    }
}
