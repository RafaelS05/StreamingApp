package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status")
    private Long idStatus;

    @Column(name = "name")
    private String name;

}
