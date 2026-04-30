package com.pstreaming.dto;

import lombok.*;

/*
Gracias a la migración del proyecto de monolito mvc a capas REST,
Se crea la necesidad de crear DTO (DATA OBJECT TRANSFER), los cuales
nos ayudan a no exponer las entidades del proyecto.
*/

/*
De forma aclaratoria: Los arhivos request son las peticiones que hace el usuario al api.
Los response son las respuestas que devuelve el proyecto, por ejemplo en perfil de usuario
Se debe crear una clase UsuariPerfilResponse para devolver la informacion del usuario.
*/

@Data
public class UsuarioLoginRequest {
    
    private String correo;
    private String password;
    
}
