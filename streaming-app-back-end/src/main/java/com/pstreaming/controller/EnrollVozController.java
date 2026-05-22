package com.pstreaming.controller;

import com.pstreaming.domain.Usuario;
import com.pstreaming.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voz")
public class EnrollVozController {

    @Autowired
    private VoiceAuthService voiceAuthService;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/enroll/{idUsuario}")
    public ResponseEntity<Void> enrollVoice(
            @RequestParam("audio") MultipartFile audio,
            @PathVariable String idUsuario) {

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        boolean enroll = voiceAuthService.enroll(usuario, audio);
        if (!enroll) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}
