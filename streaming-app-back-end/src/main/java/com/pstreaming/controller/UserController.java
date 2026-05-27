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
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService usuarioService;
    @Autowired
    private PasswordEncoder aEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TwoFAService twoFAService;

    // User register
    @PostMapping("/register")
    public ResponseEntity<UserResponse> userRegister(
            @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.save(request));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> userTempLoginPassword(
            @RequestBody UserLoginRequest request) {
        User usuario = usuarioService.getUsuarioByCorreo(request.getEmail());

        if (usuario == null || !aEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsI userDetailsI = new UserDetailsI(usuario);
        if ("USER".equals(usuarioService.getRol(usuario))) {
            UserLoginResponse res = new UserLoginResponse();
            res.setToken(jwtService.generateTempToken(usuario));
            if ("SMS".equals(usuario.getAuthMethod().getName())) {
                twoFAService.sendVerificationCode(usuario.getEmail(), usuario.getPhone());
            }
            res.setAuthMethod(usuario.getAuthMethod().getIdMethod());
            res.setTokenType("Bearer_TEMP");
            return ResponseEntity.ok(res);
        }

        UserLoginResponse res = new UserLoginResponse();
        res.setToken(jwtService.generateToken(userDetailsI));
        res.setTokenType("Bearer");
        res.setName(usuario.getName());
        res.setRol(usuarioService.getRol(usuario));
        return ResponseEntity.ok(res);
    }
}
