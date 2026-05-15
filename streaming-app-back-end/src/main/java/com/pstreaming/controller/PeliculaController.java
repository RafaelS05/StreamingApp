package com.pstreaming.controller;

import com.pstreaming.dto.*;
import com.pstreaming.service.PeliculaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pelicula")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    @GetMapping("/list")
    public ResponseEntity<List<PeliculaResponse>> movieList() {
        return ResponseEntity.ok(peliculaService.listaPeliculas());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PeliculaResponse> saveMovie(
            @RequestPart("datos") PeliculaCreateRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(peliculaService.saveMovie(request, imagenFile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponse> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.findById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PeliculaResponse> updateMovie(
            @PathVariable Long id,
            @RequestPart("datos") PeliculaUpdateRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile) {
        return ResponseEntity.ok(peliculaService.updateMovie(id, request, imagenFile));
    }

    @PatchMapping("/{id}/estado/{estado}")
    public ResponseEntity<PeliculaResponse> changeStatus(
            @PathVariable Long id,
            @PathVariable String estado) {
        return ResponseEntity.ok(peliculaService.changeStatus(id, estado));
    }
}
