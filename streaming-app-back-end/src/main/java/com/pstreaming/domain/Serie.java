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

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "año")
    private LocalDate año;

    @Column(name = "temporadas")
    private int temporadas;

    @Column(name = "episodios")
    private int episodios;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    // Relación con la tabla imagen — CascadeType.ALL para que al eliminar
    // la serie también se elimine su imagen asociada en la tabla
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_imagen", nullable = true)
    private Imagen imagen;
}
