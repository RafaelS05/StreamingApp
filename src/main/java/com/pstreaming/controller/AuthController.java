package com.pstreaming.controller;

import com.google.api.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    
//  se rastree una dirección de un archivo html
    @GetMapping("/dashboard")
    public String index(Authentication authentication) {
        return "dashboard"; }
}
