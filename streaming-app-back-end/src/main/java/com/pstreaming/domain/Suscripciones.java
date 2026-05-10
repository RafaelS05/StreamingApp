package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "suscripciones")
public class Suscripciones {
    
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



