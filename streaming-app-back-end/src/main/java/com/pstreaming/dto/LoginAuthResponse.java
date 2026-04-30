package com.pstreaming.dto;

import lombok.Data;

@Data
public class LoginAuthResponse {
    private String token;
    private String tipo;
}
