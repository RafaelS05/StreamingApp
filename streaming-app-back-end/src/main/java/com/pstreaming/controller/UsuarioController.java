package com.pstreaming.controller;

import com.pstreaming.domain.*;
import com.pstreaming.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.pstreaming.service.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder aEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TwoFAService twoFAService;

    // PROCESAR REGISTRO
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> userRegister(
            @RequestBody UsuarioRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.save(request));
    }

    // Login con contrasenna
    @PostMapping("/login/password")
    public ResponseEntity<UsuarioLoginResponse> userTempLoginPassword(
            @RequestBody UsuarioLoginRequest request) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(request.getCorreo());

        if (usuario == null || !aEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsI userDetailsI = new UserDetailsI(usuario);
        if ("USER".equals(usuarioService.getRol(usuario))) {
            UsuarioLoginResponse res = new UsuarioLoginResponse();
            res.setToken(jwtService.generateTempToken(usuario));
            if ("SMS".equals(usuario.getMetodoAuth().getNombre())) {
                twoFAService.sendVerificationCode(usuario.getCorreo(), usuario.getTelefono());
            }
            res.setMetodoAuth(usuario.getMetodoAuth().getIdMetodo());
            res.setTipo("Bearer_TEMP");
            return ResponseEntity.ok(res);
        }

        UsuarioLoginResponse res = new UsuarioLoginResponse();
        res.setToken(jwtService.generateToken(userDetailsI));
        res.setTipo("Bearer");
        res.setNombre(usuario.getNombre());
        res.setRol(usuarioService.getRol(usuario));
        return ResponseEntity.ok(res);
    }
}
