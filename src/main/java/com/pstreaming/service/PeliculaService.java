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
    private PeliculaRepository peliculaRepository;
    
    @Transactional(readOnly = true)
    public List<Pelicula> listaPeliculas() {
        return peliculaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Pelicula getPelicula(Pelicula pelicula){
        return peliculaRepository.
                findById(pelicula.getId_pelicula()).orElse(null);
        
    }
    @Transactional (readOnly = true)
    public Pelicula getPeliculaByID(Long id){
        return peliculaRepository.findById(id).orElse(null);
    }
    @Transactional(readOnly = true)
    public Pelicula getPeliculaByTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return null;
        }
        return peliculaRepository.findByTitulo(titulo);
    }
    
    
    @Transactional
    public Pelicula save(Pelicula Pelicula) {
        if (Pelicula == null) {
            throw new IllegalArgumentException("La Pelicula no puede ser null");
        }
        
        if (Pelicula.getTitulo()!= null) {
            Pelicula.setTitulo(Pelicula.getTitulo().trim().toLowerCase());
        }
        
        return peliculaRepository.save(Pelicula);
    }
    
    @Transactional
    public void delete(Pelicula Pelicula) {
        peliculaRepository.delete(Pelicula);
    }

    @Transactional
    public Pelicula actualizar(Pelicula Pelicula) {
        if (Pelicula == null || Pelicula.getId_pelicula()== null) {
            throw new IllegalArgumentException("El Pelicula y su ID no pueden ser null");
        }
        
        Optional<Pelicula> PeliculaExistente = peliculaRepository.findById(Pelicula.getId_pelicula());
        if (PeliculaExistente.isEmpty()) {
            throw new IllegalArgumentException("Pelicula no encontrado con ID: " + Pelicula.getId_pelicula());
        }
        
        if (Pelicula.getTitulo()!= null) {
            Pelicula.setTitulo(Pelicula.getTitulo().trim().toLowerCase());
        }
        
        return peliculaRepository.save(Pelicula);
    }
}