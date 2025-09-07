package com.pstreaming.controller;

import com.pstreaming.domain.Categoria;
import com.pstreaming.domain.Pelicula;
import com.pstreaming.service.CategoriaService;
import com.pstreaming.service.FirebaseStorageService;
import com.pstreaming.service.PeliculaService;
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
        model.addAttribute("pelicula", new Pelicula());
        return "pelicula/pelicula";
    }
    
    @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    @Autowired
    private MessageSource messageSource;
    
    @PostMapping("/guardar")
    public String guardar(Pelicula pelicula, 
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttributes){
        if (!imagenFile.isEmpty()) {
            peliculaService.save(pelicula);
            String ruta_imagen = firebaseStorageService.cargaImagen(imagenFile, "pelicula", pelicula.getId_pelicula());
                    pelicula.setRuta_imagen(ruta_imagen);
        }
        peliculaService.save(pelicula);
        redirectAttributes.addFlashAttribute("error", messageSource.getMessage("pelicula.error", null, Locale.getDefault()));
        return "redirect:/pelicula/pelicula";
    }
    
}
