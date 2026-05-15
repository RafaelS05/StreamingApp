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
    private EstadoRepository estadoRepository;
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
        Estado estado = estadoRepository.findByNombre("ACTIVO");
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        saveSerie.setTitulo(request.getTitulo());
        saveSerie.setAño(request.getAño());
        saveSerie.setTemporadas(request.getTemporadas());
        saveSerie.setEpisodios(request.getEpisodios());
        saveSerie.setDescripcion(request.getDescripcion());
        saveSerie.setCategoria(categoria);
        saveSerie.setEstado(estado);
        serieRepository.save(saveSerie);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            Imagen img = new Imagen();
            img.setRutaFirebase(firebaseService.cargaImagen(imagenFile, "serie", saveSerie.getIdSerie()));
            img.setNombreArchivo(imagenFile.getOriginalFilename());
            img.setFechaCarga(LocalDateTime.now());
            saveSerie.setImagen(img);
            serieRepository.save(saveSerie);
        }
        return toResponse(saveSerie);
    }

    @Transactional
    public SerieResponse updateSerie(Long id, SerieUpdateRequest updateRequest, MultipartFile imagenFile) {
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
        Estado estado = estadoRepository.findByNombre(estadoNombre);
        if (estado == null) {
            throw new RuntimeException("Estado no encontrado: " + estadoNombre);
        }
        serie.setEstado(estado);
        serieRepository.save(serie);
        return toResponse(serie);
    }

    /* Utilities */
    private SerieResponse toResponse(Serie serie) {
        SerieResponse res = new SerieResponse();
        res.setIdSerie(serie.getIdSerie());
        res.setTitulo(serie.getTitulo());
        res.setTemporadas(serie.getTemporadas());
        res.setEpisodios(serie.getEpisodios());
        res.setAño(serie.getAño());
        res.setDescripcion(serie.getDescripcion());
        res.setCategoria(serie.getCategoria().getNombre());
        res.setRutaImagen(serie.getImagen() != null ? serie.getImagen().getRutaFirebase() : null);
        res.setEstado(serie.getEstado().getNombre());
        
        return res;
    }
}
