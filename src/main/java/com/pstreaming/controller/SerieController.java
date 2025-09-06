package com.pstreaming.controller;

import com.pstreaming.domain.Categoria;
import com.pstreaming.domain.Serie;
import com.pstreaming.service.CategoriaService;
import com.pstreaming.service.SerieService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/serie")
public class SerieController {

    @Autowired
    private SerieService serieService;
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/serie")
    public String serie(Model model) {
        List<Serie> series = serieService.listaSeries();
        List<Categoria> categorias = categoriaService.listaCategorias();
        
        Map<Long, String> categoriasMap = categorias.stream()
                .collect(Collectors.toMap(
                        Categoria::getId_categoria,
                        Categoria::getNombre));
        
        model.addAttribute("series", series);
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriasMap", categoriasMap);
        return "serie/serie";
    }

}
