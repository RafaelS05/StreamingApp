package com.pstreaming.service;

import com.pstreaming.domain.*;
import com.pstreaming.dto.*;
import com.pstreaming.repository.*;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SerieService {
    
    @Autowired
    private SerieRepository SerieRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private FirebaseStorageService firebaseService;
    
    @Transactional(readOnly = true)
    public List<SerieResponse> listaSeries() {
        return SerieRepository.findAll()
                .stream().map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Serie getSerieByTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return null;
        }
        return SerieRepository.findByTitulo(titulo);
    }
    
    @Transactional(readOnly = true)
    public Serie getSerieByID(Long id){
        return SerieRepository.findById(id).orElse(null);
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
    public void delete(Serie Serie) {
        SerieRepository.delete(Serie);
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
    
    /* Utilities */
    private SerieResponse toResponse(Serie s){
        SerieResponse res = new SerieResponse();
        res.setIdSerie(s.getIdSerie());
        res.setTitulo(s.getTitulo());
        res.setTemporadas(s.getTemporadas());
        res.setEpisodios(s.getEpisodios());
        res.setAño(s.getAño());
        res.setDescripcion(s.getDescripcion());
        res.setIdCategoria(s.getCategoria().getIdCategoria());
        res.setRutaImagen(s.getImagen() != null ? s.getImagen().getRutaFirebase() : null);
        
        return res;
    }
}