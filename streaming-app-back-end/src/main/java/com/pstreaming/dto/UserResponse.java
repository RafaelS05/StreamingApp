package com.pstreaming.dto;

import lombok.Data;

@Data
public class UserResponse {
    
    private String idUsuario;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String phone;
    private String status;
    private Long authMethod;

}
