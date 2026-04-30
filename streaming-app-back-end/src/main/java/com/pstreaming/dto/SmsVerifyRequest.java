package com.pstreaming.dto;

import lombok.Data;

@Data
public class SmsVerifyRequest {

    // Token temporal generado en el login cuando se detecta que el usuario requiere 2FA
    // Identifica al usuario sin necesidad de sesión
    private String tempToken;

    // Código de 6 dígitos que el usuario recibió por SMS y debe ingresar para verificar
    private String codigo;
}
