package com.pstreaming.dto;

import lombok.Data;

@Data
public class UsuarioLoginResponse {

    private String nombre;
    private String rol;
    private String metodo2fa;
    private String token;
    private String tipo;
}
