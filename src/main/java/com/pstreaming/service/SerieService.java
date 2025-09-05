package com.pstreaming.service;

import com.pstreaming.domain.Serie;
import com.pstreaming.repository.SerieRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SerieService {
    
    @Autowired
    private SerieRepository SerieRepository;
    
    @Transactional(readOnly = true)
    public List<Serie> listaSeries() {
        return SerieRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Serie getPeliculaByTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return null;
        }
        return SerieRepository.findByTitulo(titulo);
    }
   
    
    @Transactional
    public Serie save(Serie serie) {
        if (serie == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        
        if (serie.getTitulo()!= null) {
            serie.setTitulo(serie.getTitulo().trim().toLowerCase());
        }
        
        return SerieRepository.save(serie);
    }
    
    @Transactional
    public boolean delete(Serie serie) {
        if (serie == null) {
            return false;
        }
        
        try {
            SerieRepository.delete(serie);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            if (!SerieRepository.existsById(id)) {
                return false;
            }
            
            SerieRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario por ID: " + e.getMessage());
            return false;
        }
    }
}