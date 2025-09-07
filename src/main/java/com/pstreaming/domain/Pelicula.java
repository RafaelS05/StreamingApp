package com.pstreaming.domain;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "pelicula")
public class Pelicula {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Long id_pelicula;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "año")
    private int año;

    @Column(name = "ruta_imagen")
    private String ruta_imagen;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

}
