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
    private CategoriaRepository categoriaRepository;
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
        Serie newSerie = new Serie();
        
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        
        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "serie", newSerie.getIdSerie()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            serieRepository.save(newSerie);
        }
        
        newSerie.setTitulo(request.getTitulo());
        newSerie.setAño(request.getAño());
        newSerie.setTemporadas(request.getTemporadas());
        newSerie.setEpisodios(request.getEpisodios());
        newSerie.setDescripcion(request.getDescripcion());
        newSerie.setCategoria(categoria);
        serieRepository.save(newSerie);
        
        return toResponse(newSerie);
    }
    
    @Transactional
    public SerieResponse updateSerie(Long id, SerieUpdateRequest updateRequest, MultipartFile imagenFile){
        Serie updateSerie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
               if (updateRequest.getTitulo() != null) {
            updateSerie.setTitulo(updateRequest.getTitulo());
        }
               
        if (updateRequest.getAño() != null) {
            updateSerie.setAño(updateRequest.getAño());
        }
        
        if (updateRequest.getTemporadas() != 0) {
            updateSerie.setTemporadas(updateRequest.getTemporadas());
        }
        
        if (updateRequest.getEpisodios() != 0) {
            updateSerie.setEpisodios(updateRequest.getEpisodios());
        }
        
        if (updateRequest.getDescripcion() != null) {
            updateSerie.setDescripcion(updateRequest.getDescripcion());
        }
        
        if (updateRequest.getIdCategoria() != null) {
            Categoria ct = categoriaRepository.findById(updateRequest.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Cetgoria no encontrada"));
            updateSerie.setCategoria(ct);
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "serie", updateSerie.getIdSerie()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            updateSerie.setImagen(img);
        }
        return null;
    }

    /* por implementar falta tabla para los estados por separado */
    @Transactional
    public SerieResponse changeStatus() {

        return null;
    }

    /* Utilities */
    private SerieResponse toResponse(Serie s) {
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
