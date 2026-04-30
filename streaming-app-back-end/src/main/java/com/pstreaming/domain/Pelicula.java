package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

import lombok.Data;

@Data
@Entity
@Table(name = "pelicula")
public class Pelicula {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Long idPelicula;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "año")
    private LocalDate año;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    // Relación con la tabla imagen — CascadeType.ALL para que al eliminar
    // la película también se elimine su imagen asociada en la tabla
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_imagen", nullable = true)
    private Imagen imagen;
}
