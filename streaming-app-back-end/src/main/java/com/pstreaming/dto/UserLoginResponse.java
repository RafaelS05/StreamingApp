package com.pstreaming.dto;

import lombok.Data;

@Data
public class UserLoginResponse {
    
    private String idUsuario;
    private String name;
    private String rol;
    private Long authMethod;
    private String token;
    private String tokenType;
}
