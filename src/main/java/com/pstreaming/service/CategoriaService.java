package com.pstreaming.service;

import com.pstreaming.domain.Categoria;
import com.pstreaming.repository.CategoriaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Transactional(readOnly = true)
    public List<Categoria> listaCategorias() {
        return categoriaRepository.findAll();
    }
    
    @Transactional
    public Categoria save(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria no puede ser null");
        }
        
        if (categoria.getNombre()!= null) {
            categoria.setNombre(categoria.getNombre());
        }
        
        return categoriaRepository.save(categoria);
    }
    
//    @Transactional
//    public static boolean delete(Categoria categoria) {
//        if (categoria == null) {
//            return false;
//        }
//        
//        try {
//            CategoriaRepository.delete(categoria);
//            return true;
//        } catch (Exception e) {
//            System.err.println("Error al eliminar Categoria: " + e.getMessage());
//            return false;
//        }
//    }
}