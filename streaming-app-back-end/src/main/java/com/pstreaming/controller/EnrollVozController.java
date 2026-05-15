package com.pstreaming.controller;

import com.pstreaming.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrrollVoz")
public class EnrollVozController {
    
    @Autowired
    private VoiceAuthService voiceAuthService;
    
//    @PostMapping("/registrar-voz")
//    public 
//    
}
