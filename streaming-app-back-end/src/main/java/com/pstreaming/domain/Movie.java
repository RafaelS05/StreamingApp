package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

import lombok.Data;

@Data
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movie")
    private Long idMovie;

    @Column(name = "title")
    private String title;

    @Column(name = "publish_year")
    private LocalDate publishYear;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    // Relación con la tabla imagen — CascadeType.ALL para que al eliminar
    // la película también se elimine su imagen asociada en la tabla
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_image", nullable = true)
    private Image image;
}
