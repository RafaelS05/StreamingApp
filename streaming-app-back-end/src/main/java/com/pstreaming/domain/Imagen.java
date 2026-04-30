package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "imagen")
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idImagen;
    
    @Column(name = "ruta_firebase")
    private String rutaFirebase;
    
    @Column(name = "nombre_archivo")
    private String nombreArchivo;
    
    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga;
}
