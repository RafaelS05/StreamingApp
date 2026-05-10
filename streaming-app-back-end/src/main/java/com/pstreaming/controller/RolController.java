package com.pstreaming.controller;

import com.pstreaming.domain.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class RolController {

    @ModelAttribute("esAdmin")
    public boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }

        return usuario.getRol().equals("ADMIN");

    }

    @ModelAttribute("esUser")
    public boolean esUser(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }

        return usuario.getRol().equals("USER");
    }

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioLogueado");
    }
}
