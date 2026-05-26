package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category")
    private Long idCategory;

    @Column(name = "name")
    private String name;

    //@Column(name = "descripcion")
    //private String descripcion;
}
