package com.pstreaming.dto;

import lombok.*;

/*
De forma aclaratoria: Los arhivos request son las peticiones que hace el usuario al api.
Los response son las respuestas que devuelve el proyecto, por ejemplo en perfil de usuario
Se debe crear una clase UsuariPerfilResponse para devolver la informacion del usuario.
 */
@Data
public class UsuarioRegistroRequest {

    private String nombre;
    private String apellido_1;
    private String correo;
    private String password;
    private String telefono;
    private String palabraClave;

}
