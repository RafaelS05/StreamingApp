package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario", columnDefinition = "VARCHAR(100)")
    private String idUsuario;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_status")
    private Status status;

    /*
        Paso 1: Datos básicos → nombre, correo, password, telefono, palabraClave
    Paso 2: Enrollment de voz → graba frase → Spring lo manda al FastAPI → guarda confirmación
    
    1. Password → válido → genera tempToken
    2. Elige método 2FA:
   ├── SMS        → código al teléfono → verifica → JWT
   └── Voz        → graba frase → FastAPI verifica → JWT  
    
     */
    @ManyToOne
    @JoinColumn(name = "id_auth_method")
    private AuthMethod authMethod;

}
