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

    @Transactional(readOnly = true)
    public Pelicula getPelicula(Pelicula pelicula) {
        return peliculaRepository.
                findById(pelicula.getIdPelicula()).orElse(null);

    }

    @Transactional
    public PeliculaResponse saveMovie(PeliculaCreateRequest request, MultipartFile imagenFile) {
        Pelicula pS = new Pelicula();

        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "pelicula", pS.getIdPelicula()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            pS.setImagen(img);
            peliculaRepository.save(pS);
        }

        pS.setTitulo(request.getTitulo());
        pS.setAño(request.getAño());
        pS.setDescripcion(request.getDescripcion());
        pS.setCategoria(categoria);
        peliculaRepository.save(pS);

        return toResponse(pS);
    }

    @Transactional
    public void deleteMovie(Pelicula Pelicula) {
        peliculaRepository.delete(Pelicula);
    }

    @Transactional
    public PeliculaResponse updateMovie(Long id, PeliculaUpdateRequest updateRequest, MultipartFile imagenFile) {
        
        Pelicula pU = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
        if (updateRequest.getTitulo() != null) {
            pU.setTitulo(updateRequest.getTitulo());
        }
        if (updateRequest.getAño() != null) {
            pU.setAño(updateRequest.getAño());
        }
        if (updateRequest.getDescripcion() != null) {
            pU.setDescripcion(updateRequest.getDescripcion());
        }
        if (updateRequest.getIdCategoria() != null) {
            Categoria ct = categoriaRepository.findById(updateRequest.getIdCategoria())
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
