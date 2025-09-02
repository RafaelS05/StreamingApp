package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "suscripciones")
public class Suscripciones {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long idSuscripcion;
    
    @JoinColumn(name = "idUsuario")
    private Long idUsuario;
    
    @Enumerated(EnumType.STRING)   
    private TipoSuscripcion tipoSuscripcion;
    
    
    public enum TipoSuscripcion {
    BASICA, ESTANDAR, PREMIUM
}
    
}



