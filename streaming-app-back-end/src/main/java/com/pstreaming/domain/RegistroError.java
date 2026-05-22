package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "error")
public class RegistroError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_error")
    private Long idError;

    @Column(name = "mensaje")
    private String mensaje;
    
    @Column(name = "fecha")
    private LocalDateTime fecha;

}
