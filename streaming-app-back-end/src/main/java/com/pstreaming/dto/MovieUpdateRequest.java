package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class MovieUpdateRequest {

    private String titulo;
    private LocalDate año;
    private String descripcion;
    private Long idCategoria;
    private Long idEstado;

}
