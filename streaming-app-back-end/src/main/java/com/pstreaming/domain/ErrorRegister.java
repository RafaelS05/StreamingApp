package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "error")
public class ErrorRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_error")
    private Long idError;

    @Column(name = "menssage")
    private String menssage;
    
    @Column(name = "date")
    private LocalDateTime dateError;

}
