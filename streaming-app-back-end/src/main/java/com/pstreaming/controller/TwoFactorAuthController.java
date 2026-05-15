package com.pstreaming.controller;

import com.pstreaming.domain.UserDetailsI;
import com.pstreaming.domain.Usuario;
import com.pstreaming.dto.*;
import com.pstreaming.service.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api")
public class TwoFactorAuthController {

    @Autowired
    private TwoFAService twoFAService;
    @Autowired
    private VoiceAuthService voiceService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/2fa/verificar-sms")
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
        res.setNombre(usuario.getNombre());
        res.setRol(usuarioService.getRol(usuario));

        return ResponseEntity.ok(res);
    }

    //Verify
    @PostMapping("/2fa/voz")
    public ResponseEntity<UsuarioLoginResponse> verificarVoz(
            @RequestParam String tempToken,
            @RequestParam MultipartFile audio) {

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
        res.setNombre(usuario.getNombre());
        res.setRol(usuarioService.getRol(usuario));

        return ResponseEntity.ok(res);
    }

    @PostMapping("/enroll")
    public String ProcesarEnroll(@RequestParam("audio") MultipartFile audio,
            HttpSession session,
            RedirectAttributes redirect) {

        return null;
    }
}
