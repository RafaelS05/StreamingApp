package com.pstreaming.dto;

import lombok.Data;

@Data
public class UserLoginResponse {
    
    private String idUsuario;
    private String nombre;
    private String rol;
    private Long metodoAuth;
    private String token;
    private String tipo;
}
