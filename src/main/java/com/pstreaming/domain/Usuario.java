package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Long idUsuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido_1")
    private String apellido_1;

    @Column(name = "apellido_2")
    private String apellido_2;

    @Column(name = "correo", unique = true)
    private String correo;

    @Column(name = "password")
    private String password;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "ruta_imagen")
    private String ruta_imagen;

    @Column(name = "fecha_registro")
    private LocalDateTime fecha_registro;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "idUsuario", updatable = false)
    private List<Rol> roles;
}
