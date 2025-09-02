package com.pstreaming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dash")
public class dashController {
    
    @GetMapping("/")
    public String listado(){
        return "/dashboard";
    }
    
}
