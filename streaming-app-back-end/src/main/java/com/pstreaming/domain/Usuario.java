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

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    /*
        Paso 1: Datos básicos → nombre, correo, password, telefono, palabraClave
    Paso 2: Enrollment de voz → graba frase → Spring lo manda al FastAPI → guarda confirmación
    
    1. Password → válido → genera tempToken
    2. Elige método 2FA:
   ├── SMS        → código al teléfono → verifica → JWT
   └── Voz        → graba frase → FastAPI verifica → JWT  
    
     */
    @ManyToOne
    @JoinColumn(name = "id_metodo_auth")
    private MetodoAuth metodoAuth;

}
