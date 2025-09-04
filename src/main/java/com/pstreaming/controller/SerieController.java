package com.pstreaming.controller;

import com.pstreaming.domain.Serie;
import com.pstreaming.service.SerieService;
import java.util.List;
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
    
    @GetMapping("/serie")
    public String serie(Model model) {
        List<Serie> series = serieService.listaSeries();
        model.addAttribute("series", series);
        return "serie/serie";
    }

}
