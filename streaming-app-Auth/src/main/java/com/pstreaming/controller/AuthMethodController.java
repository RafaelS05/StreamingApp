package com.pstreaming.controller;

import com.pstreaming.dto.*;
import com.pstreaming.service.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metodo-auth")
public class AuthMethodController {

    @Autowired
    private AuthMethodService metodoAuthService;

    @GetMapping
    public ResponseEntity<List<AuthMethodResponse>> MetodoAuthList() {
        return ResponseEntity.ok(metodoAuthService.MetodoAuthList());
    }
}
