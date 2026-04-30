package com.pstreaming.controller;

import com.pstreaming.domain.Pelicula;
import com.pstreaming.dto.PeliculaCreateRequest;
import com.pstreaming.dto.PeliculaResponse;
import com.pstreaming.service.CategoriaService;
import com.pstreaming.service.FirebaseStorageService;
import com.pstreaming.service.PeliculaService;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/pelicula")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private MessageSource messageSource;

//  se rastree una dirección de un archivo html
    @GetMapping("/pelicula")
    public ResponseEntity<List<PeliculaResponse>> listar() {
        return ResponseEntity.ok(peliculaService.listaPeliculas());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PeliculaResponse> guardar(
            @RequestPart("datos") PeliculaCreateRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(peliculaService.save(request, imagenFile));

    }

}
