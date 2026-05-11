package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario", columnDefinition = "VARCHAR(100)")
    private String idUsuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido_1")
    private String apellido_1;

    @Column(name = "correo", unique = true)
    private String correo;

    @Column(name = "password")
    private String password;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "fecha_registro")
    private LocalDateTime fecha_registro;

    @Column(name = "palabra_Clave")
    private String palabraClave;
    
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
