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
        Category categoria = categoriaRepository.findById(request.getIdCategory())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        saveSerie.setTitle(request.getTitle());
        saveSerie.setPublishYear(request.getPublishYear());
        saveSerie.setSeasons(request.getSeasons());
        saveSerie.setEpisodes(request.getEpisodes());
        saveSerie.setDescription(request.getDescription());
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
        if (updateRequest.getTitle() != null) {
            updateSerie.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getPublishYear() != null) {
            updateSerie.setPublishYear(updateRequest.getPublishYear());
        }

        if (updateRequest.getSeasons() != 0) {
            updateSerie.setSeasons(updateRequest.getSeasons());
        }

        if (updateRequest.getEpisodes() != 0) {
            updateSerie.setEpisodes(updateRequest.getEpisodes());
        }

        if (updateRequest.getDescription() != null) {
            updateSerie.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getIdCategory() != null) {
            Category ct = categoriaRepository.findById(updateRequest.getIdCategory())
                    .orElseThrow(() -> new RuntimeException("Catgoria no encontrada"));
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
        res.setTitle(serie.getTitle());
        res.setSeasons(serie.getSeasons());
        res.setEpisodes(serie.getEpisodes());
        res.setPublishYear(serie.getPublishYear());
        res.setDescription(serie.getDescription());
        res.setCategory(serie.getCategory().getName());
        res.setUrlImage(serie.getImage() != null ? serie.getImage().getFirebase() : null);
        res.setStatus(serie.getStatus().getName());
        
        return res;
    }
}
