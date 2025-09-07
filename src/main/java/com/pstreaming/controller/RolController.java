package com.pstreaming.controller;

import com.pstreaming.controller.UsuarioController;
import com.pstreaming.domain.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class RolController {

    @ModelAttribute("esAdmin")
    public boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || usuario.getRoles() == null) {
            return false;
        }

        return usuario.getRoles().stream()
                .anyMatch(rol -> "ADMIN".equals(rol.getNombre()));

    }

    @ModelAttribute("esUser")
    public boolean esUser(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || usuario.getRoles() == null) {
            return false;
        }

        return usuario.getRoles().stream()
                .anyMatch(rol -> "USER".equals(rol.getNombre()));

    }

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioLogueado");
    }
}
