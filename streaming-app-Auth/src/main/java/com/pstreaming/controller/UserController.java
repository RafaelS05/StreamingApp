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
    public ResponseEntity<UserLoginResponse> userLoginPassword(
            @RequestBody UserLoginRequest request) {
        User usuario = usuarioService.getUsuarioByCorreo(request.getEmail());

        if (usuario == null || !aEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        /* 
        Este primer bloque se puede mejorar para utilizar una funcion para buscar el rol y el tipo de 2FA
        Pero este crea un userDetailsI con la info de usuario
        donde si el usuario tiene el rol USER crea una respuesta para el login
        seguido se le setea el token temporal
        se consulta el metodo de verificacion (Esta hardcodeado ya que solo permite el SMS, debo modificar
        para permitirl el de VOZ)
        a la respuesta creada anteriormente se le asigna el metodo de auth (SMS hardcodeado)
        se le asigna el tipo de token y se devuelve la respuesta.
        
        */
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
        /*
        Este siguiente hace lo "contrario"
        ya que si no es "USER" asigna a la respuesta el token general
        con el userDetailsI
        la respuesta se le setea el tipo de token
        y los datos necesarios para el login
        (Nombre y Rol)
        
        
        Se puede crear un endpoint para devolver la info del usuario para el perfil de usuario
        
        */
        UserLoginResponse res = new UserLoginResponse();
        res.setToken(jwtService.generateToken(userDetailsI));
        res.setTokenType("Bearer");
        res.setName(usuario.getName());
        res.setRol(usuarioService.getRol(usuario));
        return ResponseEntity.ok(res);
    }
}
