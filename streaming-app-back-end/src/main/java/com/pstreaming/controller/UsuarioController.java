package com.pstreaming.controller;

import com.pstreaming.domain.Usuario;
import com.pstreaming.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.pstreaming.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // PROCESAR REGISTRO
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> userRegister(
            @RequestBody UsuarioRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.save(request));
    }

    // Login con contrasenna
    @PostMapping("/login/password")
    public ResponseEntity<UsuarioLoginResponse> userLoginPassword(
            @RequestBody UsuarioLoginRequest request) {
        Usuario usuario = usuarioService.getUsuatioByCorreo(request.getCorreo());
        return null;
        

    }
}
