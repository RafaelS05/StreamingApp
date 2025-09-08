package com.pstreaming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    
//  se rastree una dirección de un archivo html
    @GetMapping("/index")
    public String index(Model model) {
        return "index"; }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/index";
    }
}
