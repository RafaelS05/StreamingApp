package com.pstreaming.dto;

import lombok.Data;

@Data
public class UsuarioLoginResponse {
    private String token;
    private String tipo;
    private String rol;
    private String nombre;
}
