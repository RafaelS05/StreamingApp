package com.pstreaming.dto;

import lombok.*;

/*
De forma aclaratoria: Los arhivos request son las peticiones que hace el usuario al api.
Los response son las respuestas que devuelve el proyecto, por ejemplo en perfil de usuario
Se debe crear una clase UsuariPerfilResponse para devolver la informacion del usuario.
 */
@Data
public class UserRegisterRequest {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String phone;
    private Long authMethod;

}
