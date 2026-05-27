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
public class MovieService {

    @Autowired
    private MovieRepository peliculaRepository;
    @Autowired
    private CategoryRepository categoriaRepository;
    @Autowired
    private StatusRepository estadoRepository;
    @Autowired
    private FirebaseStorageService firebaseService;

    @Transactional(readOnly = true)
    public List<MovieResponse> listaPeliculas() {
        return peliculaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MovieResponse saveMovie(MovieCreateRequest rq, MultipartFile imagenFile) {
        Movie saveMovie = new Movie();
        Status estado = estadoRepository.findByName("ACTIVO");
        Category ct = categoriaRepository.findById(rq.getIdCategory())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        saveMovie.setTitle(rq.getTitle());
        saveMovie.setPublishYear(rq.getPublishYear());
        saveMovie.setDescription(rq.getDescription());
        saveMovie.setCategory(ct);
        saveMovie.setStatus(estado);
        peliculaRepository.save(saveMovie);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Image img = new Image();
            img.setFirebase(firebaseService.cargaImagen(imagenFile, "pelicula", saveMovie.getIdMovie()));
            img.setDocName(imagenFile.getOriginalFilename());
            img.setUploadDate(LocalDateTime.now());
            saveMovie.setImage(img);
            peliculaRepository.save(saveMovie);
        }
        return toResponse(saveMovie);
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieUpdateRequest updateRq, MultipartFile imagenFile) {

        Movie updateMovie = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
        if (updateRq.getTitle() != null) {
            updateMovie.setTitle(updateRq.getTitle());
        }
        if (updateRq.getPublishYear()!= null) {
            updateMovie.setPublishYear(updateRq.getPublishYear());
        }
        if (updateRq.getDescription() != null) {
            updateMovie.setDescription(updateRq.getDescription());
        }
        if (updateRq.getIdCategory()!= null) {
            Category ct = categoriaRepository.findById(updateRq.getIdCategory())
                    .orElseThrow(() -> new RuntimeException("Cetgoria no encontrada"));
            updateMovie.setCategory(ct);
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Image img = new Image();
            img.setFirebase(firebaseService.cargaImagen(imagenFile, "pelicula", updateMovie.getIdMovie()));
            img.setDocName(imagenFile.getOriginalFilename());
            img.setUploadDate(LocalDateTime.now());
            updateMovie.setImage(img);
        }

        peliculaRepository.save(updateMovie);
        return toResponse(updateMovie);
    }

    @Transactional(readOnly = true)
    public MovieResponse findById(Long id) {
        Movie pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
        return toResponse(pelicula);
    }

    @Transactional
    public MovieResponse changeStatus(Long id, String estadoNombre) {
        Movie pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
        Status estado = estadoRepository.findByName(estadoNombre);
        if (estado == null) {
            throw new RuntimeException("Estado no encontrado: " + estadoNombre);
        }
        pelicula.setStatus(estado);
        peliculaRepository.save(pelicula);
        return toResponse(pelicula);
    }

    /* Utilities */
    private MovieResponse toResponse(Movie movie) {
        MovieResponse res = new MovieResponse();
        res.setIdMovie(movie.getIdMovie());
        res.setTitle(movie.getTitle());
        res.setPublishYear(movie.getPublishYear());
        res.setDescription(movie.getDescription());
        res.setCategory(movie.getCategory().getName());
        res.setUrlImage(movie.getImage() != null ? movie.getImage().getFirebase() : null);
        res.setStatus(movie.getStatus().getName());

        return res;
    }
}
