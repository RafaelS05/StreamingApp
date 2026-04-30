package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class PeliculaUpdateRequest {
    
    private String titulo;
    private LocalDate año;
    private String descripcion;
    private Long idCategoria;
    
}
