package com.pstreaming.controller;

import com.pstreaming.domain.UserDetailsI;
import com.pstreaming.domain.Usuario;
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
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/verificar-sms")
    public ResponseEntity<UsuarioLoginResponse> verificarSMS(
            @RequestBody SmsVerifyRequest request) {

        if (!jwtService.isTempToken(request.getTempToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String correo = jwtService.extractUsername(request.getTempToken());
        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);

        String codigo = request.getCodigo();

        if (!twoFAService.verifyCode(correo, codigo)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsI userDetailsI = new UserDetailsI(usuario);

        UsuarioLoginResponse res = new UsuarioLoginResponse();
        res.setToken(jwtService.generateToken(userDetailsI));
        res.setTipo("Bearer");
        res.setIdUsuario(usuario.getIdUsuario());
        res.setNombre(usuario.getNombre());
        res.setRol(usuarioService.getRol(usuario));

        return ResponseEntity.ok(res);
    }

    //Verify
    @PostMapping("/voz")
    public ResponseEntity<UsuarioLoginResponse> verificarVoz(
            @RequestHeader("X-Temp-Token") String tempToken,
            @RequestParam MultipartFile audio) {

        System.out.println("=== tempToken received: '" + tempToken + "'");
        System.out.println("=== tempToken length: " + tempToken.length());

        if (!jwtService.isTempToken(tempToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String correo = jwtService.extractUsername(tempToken);
        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);

        if (!voiceService.verify(usuario, audio)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsI userDetailsI = new UserDetailsI(usuario);

        UsuarioLoginResponse res = new UsuarioLoginResponse();
        res.setToken(jwtService.generateToken(userDetailsI));
        res.setTipo("Bearer");
        res.setIdUsuario(usuario.getIdUsuario());
        res.setNombre(usuario.getNombre());
        res.setRol(usuarioService.getRol(usuario));

        return ResponseEntity.ok(res);
    }
}
