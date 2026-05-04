package com.pstreaming.dto;

import lombok.Data;

@Data
public class UsuarioResponse {
    
    private Long idUsuario;
    private String nombre;
    private String apellido_1;
    private String correo;
    private String telefono;
    private String estado;

}
