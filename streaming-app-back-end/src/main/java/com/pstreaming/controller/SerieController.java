package com.pstreaming.controller;

import com.pstreaming.dto.*;
import com.pstreaming.service.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/serie")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @GetMapping
    public ResponseEntity<List<SerieResponse>> serieList() {
        return ResponseEntity.ok(serieService.listaSeries());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SerieResponse> saveSerie(
            @RequestPart("datos") SerieCreateRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serieService.saveSerie(request, imagenFile));

    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SerieResponse> updateSerie(
            @PathVariable Long id,
            @RequestPart("datos") SerieUpdateRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile) {
        return ResponseEntity.ok(serieService.updateSerie(id, request, imagenFile));
    }
}
