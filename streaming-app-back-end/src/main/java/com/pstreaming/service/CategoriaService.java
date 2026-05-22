package com.pstreaming.service;

import com.pstreaming.domain.Categoria;
import com.pstreaming.dto.*;
import com.pstreaming.repository.CategoriaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> CategoryList() {
        return categoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    /* Utilities */
    private CategoriaResponse toResponse(Categoria categoria) {
        CategoriaResponse res = new CategoriaResponse();
        res.setIdCategoria(categoria.getIdCategoria());
        res.setNombre(categoria.getNombre());

        return res;
    }
}
