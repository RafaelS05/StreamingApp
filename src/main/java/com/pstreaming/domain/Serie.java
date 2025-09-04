package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "serie")
public class Serie {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_serie")
    private Long id_serie;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "año")
    private int año;

    @Column(name = "ruta_imagen")
    private String ruta_imagen;

    @Column(name = "id_categoria")
    private String categoria;

}
