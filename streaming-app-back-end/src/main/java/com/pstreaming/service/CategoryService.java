package com.pstreaming.service;

import com.pstreaming.domain.Category;
import com.pstreaming.dto.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pstreaming.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> CategoryList() {
        return categoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    /* Utilities */
    private CategoryResponse toResponse(Category categoria) {
        CategoryResponse res = new CategoryResponse();
        res.setIdCategory(categoria.getIdCategory());
        res.setName(categoria.getName());

        return res;
    }
}
