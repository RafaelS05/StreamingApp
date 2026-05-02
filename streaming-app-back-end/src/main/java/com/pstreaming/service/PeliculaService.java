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

    @Transactional
    public PeliculaResponse saveMovie(PeliculaCreateRequest rq, MultipartFile imagenFile) {
        Pelicula pS = new Pelicula();

        Categoria ct = categoriaRepository.findById(rq.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "pelicula", pS.getIdPelicula()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            pS.setImagen(img);
            peliculaRepository.save(pS);
        }

        pS.setTitulo(rq.getTitulo());
        pS.setAño(rq.getAño());
        pS.setDescripcion(rq.getDescripcion());
        pS.setCategoria(ct);
        peliculaRepository.save(pS);

        return toResponse(pS);
    }
    
    @Transactional
    public PeliculaResponse updateMovie(Long id, PeliculaUpdateRequest updateRq, MultipartFile imagenFile) {
        
        Pelicula pU = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
        if (updateRq.getTitulo() != null) {
            pU.setTitulo(updateRq.getTitulo());
        }
        if (updateRq.getAño() != null) {
            pU.setAño(updateRq.getAño());
        }
        if (updateRq.getDescripcion() != null) {
            pU.setDescripcion(updateRq.getDescripcion());
        }
        if (updateRq.getIdCategoria() != null) {
            Categoria ct = categoriaRepository.findById(updateRq.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Cetgoria no encontrada"));
            pU.setCategoria(ct);
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "pelicula", pU.getIdPelicula()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            pU.setImagen(img);
        }

        peliculaRepository.save(pU);
        return toResponse(pU);
    }
    
    /* por implementar falta tabla para los estados por separado */
    @Transactional
    public PeliculaResponse changeStatus(){
        
        return null;
    }

    /* Utilities */
    private PeliculaResponse toResponse(Pelicula pelicula) {
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
