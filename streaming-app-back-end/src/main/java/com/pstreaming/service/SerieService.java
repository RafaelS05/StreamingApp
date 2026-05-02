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
    public SerieResponse saveSerie(SerieCreateRequest rq, MultipartFile imagenFile) {
        Serie sS = new Serie();
        
        Categoria ct = categoriaRepository.findById(rq.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        
        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "serie", sS.getIdSerie()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            serieRepository.save(sS);
        }
        sS.setTitulo(rq.getTitulo());
        sS.setAño(rq.getAño());
        sS.setTemporadas(rq.getTemporadas());
        sS.setEpisodios(rq.getEpisodios());
        sS.setDescripcion(rq.getDescripcion());
        sS.setCategoria(ct);
        serieRepository.save(sS);
        
        return toResponse(sS);
    }
    
    @Transactional
    public SerieResponse updateSerie(Long id, SerieUpdateRequest updateRq, MultipartFile imagenFile){
        Serie sU = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
               if (updateRq.getTitulo() != null) {
            sU.setTitulo(updateRq.getTitulo());
        }
        if (updateRq.getAño() != null) {
            sU.setAño(updateRq.getAño());
        }
        if (updateRq.getTemporadas() != 0) {
            sU.setTemporadas(updateRq.getTemporadas());
        }
        if (updateRq.getEpisodios() != 0) {
            sU.setEpisodios(updateRq.getEpisodios());
        }
        if (updateRq.getDescripcion() != null) {
            sU.setDescripcion(updateRq.getDescripcion());
        }
        if (updateRq.getIdCategoria() != null) {
            Categoria ct = categoriaRepository.findById(updateRq.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Cetgoria no encontrada"));
            sU.setCategoria(ct);
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "serie", sU.getIdSerie()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            sU.setImagen(img);
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
