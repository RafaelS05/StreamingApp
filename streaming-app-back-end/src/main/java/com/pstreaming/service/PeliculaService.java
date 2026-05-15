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
    private EstadoRepository estadoRepository;
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
        Pelicula saveMovie = new Pelicula();
        Estado estado = estadoRepository.findByNombre("ACTIVO");
        Categoria ct = categoriaRepository.findById(rq.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        saveMovie.setTitulo(rq.getTitulo());
        saveMovie.setAño(rq.getAño());
        saveMovie.setDescripcion(rq.getDescripcion());
        saveMovie.setCategoria(ct);
        saveMovie.setEstado(estado);
        peliculaRepository.save(saveMovie);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "pelicula", saveMovie.getIdPelicula()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            saveMovie.setImagen(img);
            peliculaRepository.save(saveMovie);
        }
        return toResponse(saveMovie);
    }

    @Transactional
    public PeliculaResponse updateMovie(Long id, PeliculaUpdateRequest updateRq, MultipartFile imagenFile) {

        Pelicula updateMovie = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
        if (updateRq.getTitulo() != null) {
            updateMovie.setTitulo(updateRq.getTitulo());
        }
        if (updateRq.getAño() != null) {
            updateMovie.setAño(updateRq.getAño());
        }
        if (updateRq.getDescripcion() != null) {
            updateMovie.setDescripcion(updateRq.getDescripcion());
        }
        if (updateRq.getIdCategoria() != null) {
            Categoria ct = categoriaRepository.findById(updateRq.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Cetgoria no encontrada"));
            updateMovie.setCategoria(ct);
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "pelicula", updateMovie.getIdPelicula()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            updateMovie.setImagen(img);
        }

        peliculaRepository.save(updateMovie);
        return toResponse(updateMovie);
    }

    @Transactional(readOnly = true)
    public PeliculaResponse findById(Long id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
        return toResponse(pelicula);
    }

    @Transactional
    public PeliculaResponse changeStatus(Long id, String estadoNombre) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
        Estado estado = estadoRepository.findByNombre(estadoNombre);
        if (estado == null) {
            throw new RuntimeException("Estado no encontrado: " + estadoNombre);
        }
        pelicula.setEstado(estado);
        peliculaRepository.save(pelicula);
        return toResponse(pelicula);
    }

    /* Utilities */
    private PeliculaResponse toResponse(Pelicula pelicula) {
        PeliculaResponse res = new PeliculaResponse();
        res.setIdPelicula(pelicula.getIdPelicula());
        res.setTitulo(pelicula.getTitulo());
        res.setAño(pelicula.getAño());
        res.setDescripcion(pelicula.getDescripcion());
        res.setCategoria(pelicula.getCategoria().getNombre());
        res.setRutaImagen(pelicula.getImagen() != null ? pelicula.getImagen().getRutaFirebase() : null);
        res.setEstado(pelicula.getEstado().getNombre());

        return res;
    }
}
