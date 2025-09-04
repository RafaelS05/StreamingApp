package com.pstreaming.controller;

import com.pstreaming.domain.Pelicula;
import com.pstreaming.service.PeliculaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pelicula")
public class PeliculaController {
    
    @Autowired
    private PeliculaService peliculaService;
    
//  se rastree una dirección de un archivo html
     @GetMapping("/pelicula")
    public String pelicula(Model model){
        List<Pelicula> peliculas = peliculaService.listaPeliculas();
        model.addAttribute("peliculas", peliculas);
        return "pelicula/pelicula";
    }
}
