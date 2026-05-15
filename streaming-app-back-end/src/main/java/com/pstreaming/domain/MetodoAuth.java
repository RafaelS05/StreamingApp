package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "metodo_auth")
public class MetodoAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_auth")
    private Long idMetodo;
    @Column(name = "nombre")
    private String nombre;

}
