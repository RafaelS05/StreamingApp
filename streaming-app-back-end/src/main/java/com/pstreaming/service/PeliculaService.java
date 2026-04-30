package com.pstreaming.service;

import com.pstreaming.domain.Categoria;
import com.pstreaming.domain.Imagen;
import com.pstreaming.domain.Pelicula;
import com.pstreaming.dto.PeliculaResponse;
import com.pstreaming.repository.PeliculaRepository;
import com.pstreaming.repository.CategoriaRepository;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PeliculaService {
    
    @Autowired
    private PeliculaRepository peliculaRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private FirebaseStorageService firebaseService;
    
    @Transactional(readOnly = true)
    public List<PeliculaResponse> listaPeliculas() {
        return peliculaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Pelicula getPelicula(Pelicula pelicula){
        return peliculaRepository.
                findById(pelicula.getIdPelicula()).orElse(null);
        
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
    public PeliculaResponse save(PeliculaResponse request, MultipartFile imagenFile) {
        Pelicula p = new Pelicula();
        p.setTitulo(request.getTitulo());
        p.setAño(request.getAño());
        p.setDescripcion(request.getDescripcion());
        
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        peliculaRepository.save(p);
        
        if(imagenFile != null && !imagenFile.isEmpty()){
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "pelicula", p.getIdPelicula()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            peliculaRepository.save(p);
        }
        return toResponse(p);
    }
    
    @Transactional
    public void delete(Pelicula Pelicula) {
        peliculaRepository.delete(Pelicula);
    }

    @Transactional
    public Pelicula actualizar(Pelicula Pelicula) {
        if (Pelicula == null || Pelicula.getIdPelicula()== null) {
            throw new IllegalArgumentException("El Pelicula y su ID no pueden ser null");
        }
        
        Optional<Pelicula> PeliculaExistente = peliculaRepository.findById(Pelicula.getIdPelicula());
        if (PeliculaExistente.isEmpty()) {
            throw new IllegalArgumentException("Pelicula no encontrado con ID: " + Pelicula.getIdPelicula());
        }
        
        if (Pelicula.getTitulo()!= null) {
            Pelicula.setTitulo(Pelicula.getTitulo().trim().toLowerCase());
        }
        
        return peliculaRepository.save(Pelicula);
    }
    
    
    /* Utilities */
    private PeliculaResponse toResponse(Pelicula pelicula){
        PeliculaResponse res = new PeliculaResponse();
        res.setIdPelicula(pelicula.getIdPelicula());
        res.setTitulo(pelicula.getTitulo());
        res.setAño(pelicula.getAño());
        res.setDescripcion(pelicula.getDescripcion());
        res.setIdCategoria(pelicula.getCategoria().getIdCategoria());
        res.setRutaImagen(pelicula.getImagen() != null ? pelicula.getImagen().getRutaFirebase() : null);
        
        return res;
    }
    
    
}