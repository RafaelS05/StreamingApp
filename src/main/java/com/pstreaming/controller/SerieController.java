package com.pstreaming.controller;

import com.pstreaming.domain.Categoria;
import com.pstreaming.domain.Pelicula;
import com.pstreaming.domain.Serie;
import com.pstreaming.service.CategoriaService;
import com.pstreaming.service.FirebaseStorageService;
import com.pstreaming.service.SerieService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("serie", new Serie());
        return "serie/serie";
    }

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private MessageSource messageSource;
    
        @PostMapping("/guardar")
    public String guardar(Serie serie, 
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttributes){
        if (!imagenFile.isEmpty()) {
            serieService.save(serie);
            String ruta_imagen = firebaseStorageService.cargaImagen(imagenFile, "serie", serie.getId_serie());
                    serie.setRuta_imagen(ruta_imagen);
        }
        serieService.save(serie);
        redirectAttributes.addFlashAttribute("error", messageSource.getMessage("serie.error", null, Locale.getDefault()));
        return "redirect:/serie/serie";
    }
}
