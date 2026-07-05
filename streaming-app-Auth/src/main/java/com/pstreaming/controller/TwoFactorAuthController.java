package com.pstreaming.controller;

import com.pstreaming.domain.UserDetailsI;
import com.pstreaming.domain.User;
import com.pstreaming.dto.*;
import com.pstreaming.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorAuthController {

    @Autowired
    private TwoFAService twoFAService;
    @Autowired
    private VoiceAuthService voiceService;
    @Autowired
    private UserService usuarioService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/sms")
    public ResponseEntity<UserLoginResponse> verificarSMS(
            @RequestBody SmsVerifyRequest request) {
        
        //Solo acepta JWT temporales
        if (!jwtService.isTempToken(request.getTempToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //Extrae el correo del token
        String correo = jwtService.extractUsername(request.getTempToken());
        //Busca el usuario por correo
        User usuario = usuarioService.getUsuarioByCorreo(correo);
        //Hace el request del codigo
        String codigo = request.getCode();
        
        //Llama al servicio de 2FA para verificar el codigo, donde si el
        //Correo existe pero el codigo es diferente al ingresado no permite la construccion
        //Se utiliza un codigo asociado a un correo, entonces cada uno es "independiente"
        
        if (!twoFAService.verifyCode(correo, codigo)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //Si el codigo introducido es correcto este se verifica y pemite la creacion del login
        //Ahora con el token no temporal
        //Aparte el codigo guardado en una lista se desvincula del correo
        //Y se elimina de la lista de codigo, por lo que no se puede volve a utilizar.
        UserDetailsI userDetailsI = new UserDetailsI(usuario);

        UserLoginResponse res = new UserLoginResponse();
        res.setToken(jwtService.generateToken(userDetailsI));
        res.setTokenType("Bearer");
        res.setIdUsuario(usuario.getIdUsuario());
        res.setName(usuario.getName());
        res.setRol(usuarioService.getRol(usuario));

        return ResponseEntity.ok(res);
    }

    //Verify
    @PostMapping("/voz")
    public ResponseEntity<UserLoginResponse> verificarVoz(
            @RequestHeader("X-Temp-Token") String tempToken,
            @RequestParam MultipartFile audio) {
        
        //Solo se permiten tokenTemporales
        if (!jwtService.isTempToken(tempToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //Extrae el contenido del token
        String correo = jwtService.extractUsername(tempToken);
        //Crea el usuario para llamar el usuario y verificar
        User usuario = usuarioService.getUsuarioByCorreo(correo);
        
        /*
        Este llama al microservicio, donde se lleva el id del usuario vinculado con la voz
        
        */
        if (!voiceService.verify(usuario, audio)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsI userDetailsI = new UserDetailsI(usuario);

        UserLoginResponse res = new UserLoginResponse();
        res.setToken(jwtService.generateToken(userDetailsI));
        res.setTokenType("Bearer");
        res.setIdUsuario(usuario.getIdUsuario());
        res.setName(usuario.getName());
        res.setRol(usuarioService.getRol(usuario));

        return ResponseEntity.ok(res);
    }
}
