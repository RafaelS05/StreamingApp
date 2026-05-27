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
    private UserRepository userRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private AuthMethodRepository authMethodRepository;
    @Autowired
    private PasswordEncoder aEncoder;

    @Transactional
    public UserResponse save(UserRegisterRequest request) {

        if (existeByCorreo(request.getEmail())) {
            throw new RuntimeException("Este correo ya se encuentra registrado.");
        }
        User user = new User();
        Status status = statusRepository.findByName("ACTIVO");
        if (status == null) {
            throw new RuntimeException("Este usuario no cuenta con un estado definido.");
        }
        user.setStatus(status);

        Rol rol = rolRepository.findByName("USER");

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setPassword(aEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        AuthMethod metodo = authMethodRepository.findById(request.getAuthMethod())
                .orElseThrow(() -> new RuntimeException("Metodo Invalido"));

        user.setAuthMethod(metodo);
        user.setRol(rol);
        user.setRegisterDate(LocalDateTime.now());
        userRepository.save(user);

        return toResponse(user);
    }

    @Transactional
    public User actualizar(User user) {
        if (user == null || user.getIdUsuario() == null) {
            throw new IllegalArgumentException("El usuario y su ID no pueden ser null");
        }

        Optional<User> usuarioExistente = userRepository.findById(user.getIdUsuario());
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + user.getIdUsuario());
        }

        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }

        return userRepository.save(user);
    }

    @Transactional
    public boolean delete(User user) {
        if (user == null) {
            return false;
        }

        try {
            userRepository.delete(user);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public User findOrCreateOAuth2User(String email, String name, String surname) {
        User user = getUsuarioByCorreo(email);
        if (user != null) {
            return user;
        }
        Status status = statusRepository.findByName("ACTIVO");
        Rol rol = rolRepository.findByName("USER");
        
        user = new User();
        user.setEmail(email.trim().toLowerCase());
        user.setName(name);
        user.setSurname(surname);
        user.setStatus(status);
        user.setRol(rol);
        user.setRegisterDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    /* Utilities */
    private UserResponse toResponse(User u) {
        UserResponse res = new UserResponse();
        res.setIdUsuario(u.getIdUsuario());
        res.setName(u.getName());
        res.setSurname(u.getSurname());
        res.setEmail(u.getEmail());
        res.setPhone(u.getPhone());
        res.setStatus(u.getStatus().getName());
        res.setAuthMethod(u.getAuthMethod().getIdMethod());

        return res;
    }

    @Transactional(readOnly = true)
    public boolean existeByCorreo(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Transactional
    public User getUsuarioByCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(correo.trim().toLowerCase());
    }

    @Transactional
    public User findById(String idUsuario) {
        return userRepository.findById(idUsuario).orElse(null);

    }

    @Transactional
    public String getRol(User user) {
        if (user.getRol() == null) {
            return "USER";
        }
        return user.getRol().getName();
    }
}
