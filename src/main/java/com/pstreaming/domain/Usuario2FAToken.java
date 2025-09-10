package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario_2fa_tokens")
public class Usuario2FAToken {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_2fa_tokens")
    private Long id_2fa_tokens;

    @Column(name = "idUsuario")
    private Long idUsuario;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "codigo_sms")
    private String codigoSms;
    
    @Column(name = "correo")
    private String correo;
    
    @Column(name = "telefono")
    private String telefono;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "usado")
    private boolean usado = false;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoToken tipo;

    
    enum TipoToken{
        EMAIL, SMS, TOTP
    }
}
