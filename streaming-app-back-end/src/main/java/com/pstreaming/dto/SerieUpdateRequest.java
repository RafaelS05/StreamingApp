package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class SerieUpdateRequest {

    private String titulo;
    private LocalDate año;
    private int temporadas;
    private int episodios;
    private String descripcion;
    private Long idCategoria;
    private Long idEstado;

}
