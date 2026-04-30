package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class SerieCreateRequest {
    
    private String titulo;
    private LocalDate año;
    private int temporadas;
    private int episodios;
    private String descripcion;
    private String idCategoria;
}
