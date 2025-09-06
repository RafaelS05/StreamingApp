package com.pstreaming.controller;

import com.pstreaming.domain.Categoria;
import com.pstreaming.domain.Pelicula;
import com.pstreaming.service.CategoriaService;
import com.pstreaming.service.PeliculaService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    @Autowired
    private CategoriaService categoriaService;

//  se rastree una dirección de un archivo html
    @GetMapping("/pelicula")
    public String pelicula(Model model) {
        List<Pelicula> peliculas = peliculaService.listaPeliculas();
        List<Categoria> categorias = categoriaService.listaCategorias();
        
        Map<Long, String> categoriasMap = categorias.stream()
                .collect(Collectors.toMap(
                        Categoria::getId_categoria,
                        Categoria::getNombre));
        
        model.addAttribute("peliculas", peliculas);
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriasMap", categoriasMap);
        return "pelicula/pelicula";
    }
}
