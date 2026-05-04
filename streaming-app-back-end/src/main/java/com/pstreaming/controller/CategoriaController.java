package com.pstreaming.controller;

import com.pstreaming.dto.*;
import com.pstreaming.service.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> categoryList() {
        return ResponseEntity.ok(categoriaService.CategoryList());
    }
}
