package com.pstreaming.dto;

import lombok.Data;

@Data
public class UserResponse {
    
    private String idUsuario;
    private String nombre;
    private String apellido_1;
    private String correo;
    private String telefono;
    private String estado;
    private Long metodoAuth;

}
