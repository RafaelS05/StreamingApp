package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "metodo_auth")
public class AuthMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auth_method")
    private Long idMethod;
    @Column(name = "name")
    private String name;

}
