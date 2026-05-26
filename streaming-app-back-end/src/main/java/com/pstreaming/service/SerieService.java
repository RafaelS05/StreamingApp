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
    private SerieRepository serieRepository;
    @Autowired
    private CategoryRepository categoriaRepository;
    @Autowired
    private StatusRepository estadoRepository;
    @Autowired
    private FirebaseStorageService firebaseService;

    @Transactional(readOnly = true)
    public List<SerieResponse> listaSeries() {
        return serieRepository.findAll()
                .stream().map(this::toResponse)
                .toList();
    }

    @Transactional
    public SerieResponse saveSerie(SerieCreateRequest request, MultipartFile imagenFile) {
        Serie saveSerie = new Serie();
        Status estado = estadoRepository.findByNombre("ACTIVO");
        Category categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        saveSerie.setTitle(request.getTitulo());
        saveSerie.setPublishYear(request.getAño());
        saveSerie.setSeasons(request.getTemporadas());
        saveSerie.setEpisodes(request.getEpisodios());
        saveSerie.setDescription(request.getDescripcion());
        saveSerie.setCategory(categoria);
        saveSerie.setStatus(estado);
        serieRepository.save(saveSerie);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Image img = new Image();
            img.setFirebase(firebaseService.cargaImagen(imagenFile, "serie", saveSerie.getIdSerie()));
            img.setDocName(imagenFile.getOriginalFilename());
            img.setUploadDate(LocalDateTime.now());
            saveSerie.setImage(img);
            serieRepository.save(saveSerie);
        }
        return toResponse(saveSerie);
    }

    @Transactional
    public SerieResponse updateSerie(Long id, SerieUpdateRequest updateRequest, MultipartFile imagenFile) {
        Serie updateSerie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
        if (updateRequest.getTitulo() != null) {
            updateSerie.setTitle(updateRequest.getTitulo());
        }

        if (updateRequest.getAño() != null) {
            updateSerie.setPublishYear(updateRequest.getAño());
        }

        if (updateRequest.getTemporadas() != 0) {
            updateSerie.setSeasons(updateRequest.getTemporadas());
        }

        if (updateRequest.getEpisodios() != 0) {
            updateSerie.setEpisodes(updateRequest.getEpisodios());
        }

        if (updateRequest.getDescripcion() != null) {
            updateSerie.setDescription(updateRequest.getDescripcion());
        }

        if (updateRequest.getIdCategoria() != null) {
            Category ct = categoriaRepository.findById(updateRequest.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Cetgoria no encontrada"));
            updateSerie.setCategory(ct);
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Image img = new Image();
            img.setFirebase(firebaseService.cargaImagen(imagenFile, "serie", updateSerie.getIdSerie()));
            img.setDocName(imagenFile.getOriginalFilename());
            img.setUploadDate(LocalDateTime.now());
            updateSerie.setImage(img);
        }
        serieRepository.save(updateSerie);
        return toResponse(updateSerie);
    }

    @Transactional(readOnly = true)
    public SerieResponse findById(Long id) {
        Serie serie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
        return toResponse(serie);
    }

    @Transactional
    public SerieResponse changeStatus(Long id, String estadoNombre) {
        Serie serie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
        Status estado = estadoRepository.findByNombre(estadoNombre);
        if (estado == null) {
            throw new RuntimeException("Estado no encontrado: " + estadoNombre);
        }
        serie.setStatus(estado);
        serieRepository.save(serie);
        return toResponse(serie);
    }

    /* Utilities */
    private SerieResponse toResponse(Serie serie) {
        SerieResponse res = new SerieResponse();
        res.setIdSerie(serie.getIdSerie());
        res.setTitulo(serie.getTitle());
        res.setTemporadas(serie.getSeasons());
        res.setEpisodios(serie.getEpisodes());
        res.setAño(serie.getPublishYear());
        res.setDescripcion(serie.getDescription());
        res.setCategoria(serie.getCategory().getName());
        res.setRutaImagen(serie.getImage() != null ? serie.getImage().getFirebase() : null);
        res.setEstado(serie.getStatus().getName());
        
        return res;
    }
}
