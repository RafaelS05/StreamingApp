package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class SerieResponse {
    private Long idSerie;
    private String titulo;
    private LocalDate año;
    private int temporadas;
    private int episodios;
    private String rutaImagen;
    private String descripcion;
    private String categoria;
    
}
