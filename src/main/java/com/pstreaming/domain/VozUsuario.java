package com.pstreaming.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "voz_usuario")
public class VozUsuario {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voz")
    private Long idVoz;
    
    @Lob
    @Column(name = "voice_print", columnDefinition = "LONGLOB")
    private byte[] voicePrint;

    @Column(name = "voz_model")
    private String vozModel;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", updatable = false, nullable = false, unique = false)
    private Usuario usuario;
    
}
