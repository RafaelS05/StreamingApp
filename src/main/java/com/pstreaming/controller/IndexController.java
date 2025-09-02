package com.pstreaming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    
//  se rastree una dirección de un archivo html
    @GetMapping("/index")
    public String index() {
        return "index"; // busca templates/index.html
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/index"; // redirecciona a /index
    }
}
