package com.pstreaming.controller;

import com.pstreaming.dto.*;
import com.pstreaming.service.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metodo-auth")
public class MetodoAuthController {

    @Autowired
    private MetodoAuthService metodoAuthService;

    @GetMapping
    public ResponseEntity<List<MetodoAuthResponse>> MetodoAuthList() {
        return ResponseEntity.ok(metodoAuthService.MetodoAuthList());
    }
}
