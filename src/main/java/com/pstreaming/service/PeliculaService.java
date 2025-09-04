package com.pstreaming.service;

import com.pstreaming.domain.Pelicula;
import com.pstreaming.repository.PeliculaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeliculaService {
    
    @Autowired
    private PeliculaRepository PeliculaRepository;
    
    @Transactional(readOnly = true)
    public List<Pelicula> listaPeliculas() {
        return PeliculaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Pelicula getPeliculaByTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return null;
        }
        return PeliculaRepository.findByTitulo(titulo);
    }
    
    @Transactional
    public Pelicula save(Pelicula Pelicula) {
        if (Pelicula == null) {
            throw new IllegalArgumentException("La Pelicula no puede ser null");
        }
        
        if (Pelicula.getTitulo()!= null) {
            Pelicula.setTitulo(Pelicula.getTitulo().trim().toLowerCase());
        }
        
        return PeliculaRepository.save(Pelicula);
    }
    
    @Transactional
    public boolean delete(Pelicula Pelicula) {
        if (Pelicula == null) {
            return false;
        }
        
        try {
            PeliculaRepository.delete(Pelicula);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar Pelicula: " + e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            if (!PeliculaRepository.existsById(id)) {
                return false;
            }
            
            PeliculaRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar Pelicula por ID: " + e.getMessage());
            return false;
        }
    }
    @Transactional
    public Pelicula actualizar(Pelicula Pelicula) {
        if (Pelicula == null || Pelicula.getId_pelicula()== null) {
            throw new IllegalArgumentException("El Pelicula y su ID no pueden ser null");
        }
        
        Optional<Pelicula> PeliculaExistente = PeliculaRepository.findById(Pelicula.getId_pelicula());
        if (PeliculaExistente.isEmpty()) {
            throw new IllegalArgumentException("Pelicula no encontrado con ID: " + Pelicula.getId_pelicula());
        }
        
        if (Pelicula.getTitulo()!= null) {
            Pelicula.setTitulo(Pelicula.getTitulo().trim().toLowerCase());
        }
        
        return PeliculaRepository.save(Pelicula);
    }
}