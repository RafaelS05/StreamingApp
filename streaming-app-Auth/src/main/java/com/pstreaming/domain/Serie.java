package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "serie")
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_serie")
    private Long idSerie;

    @Column(name = "title")
    private String title;

    @Column(name = "publish_year")
    private LocalDate publishYear;

    @Column(name = "seasons")
    private int seasons;

    @Column(name = "episodes")
    private int episodes;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private Category category;

    // Relación con la tabla imagen — CascadeType.ALL para que al eliminar
    // la serie también se elimine su imagen asociada en la tabla
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_image", nullable = true)
    private Image image;
}
