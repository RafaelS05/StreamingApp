package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "subscription")
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long idSubscription;
    
    @JoinColumn(name = "idUser")
    private Long idUser;
    
    @Enumerated(EnumType.STRING)   
    private TipoSuscripcion tipoSuscripcion;
    
    
    public enum TipoSuscripcion {
    BASICA, ESTANDAR, PREMIUM
}
    
}



