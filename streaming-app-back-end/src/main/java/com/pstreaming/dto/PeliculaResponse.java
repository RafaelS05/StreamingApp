package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class PeliculaResponse {
    private Long idPelicula;
    private String titulo;
    private LocalDate año;
    private String rutaImagen;
    private String descripcion;
    private Long idCategoria;
}
