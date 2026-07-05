package com.pstreaming.controller;

import com.pstreaming.dto.*;
import com.pstreaming.service.MovieService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService peliculaService;

    @GetMapping("/list")
    public ResponseEntity<List<MovieResponse>> movieList() {
        return ResponseEntity.ok(peliculaService.listaPeliculas());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponse> saveMovie(
            @RequestPart("datos") MovieCreateRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(peliculaService.saveMovie(request, imagenFile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.findById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable Long id,
            @RequestPart("datos") MovieUpdateRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile) {
        return ResponseEntity.ok(peliculaService.updateMovie(id, request, imagenFile));
    }

    @PatchMapping("/{id}/estado/{estado}")
    public ResponseEntity<MovieResponse> changeStatus(
            @PathVariable Long id,
            @PathVariable String estado) {
        return ResponseEntity.ok(peliculaService.changeStatus(id, estado));
    }
}
