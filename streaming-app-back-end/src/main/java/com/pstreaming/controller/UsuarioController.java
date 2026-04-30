package com.pstreaming.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pstreaming.domain.UserDetailsI;
import com.pstreaming.domain.Usuario;
import com.pstreaming.service.AuthService;
import com.pstreaming.service.TwoFAPolicyService;
import com.pstreaming.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder aEncoder;
    @Autowired
    private AuthService authService;
    @Autowired
    private TwoFAPolicyService twoFAserivice;

    // PROCESAR REGISTRO
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            if (usuarioService.existeByCorreo(usuario.getCorreo())) {
                model.addAttribute("error", "Este correo electrónico no puede ser utilizado.");
                return "usuario/registro";
            }

            usuario.setPassword(aEncoder.encode(usuario.getPassword()));
            usuario.setPalabraClave(aEncoder.encode(usuario.getPalabraClave()));
            usuario.setFecha_registro(LocalDateTime.now());

            usuarioService.save(usuario);
            


            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado exitosamente");
            return "redirect:/usuario/login";

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "usuario/registro";
        }
    }

    // Login con contrasenna
    @PostMapping("/login/password")
    public String procesarLoginContrasenna(@RequestParam String correo,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirect) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);

        if (usuario == null || !aEncoder.matches(password, usuario.getPassword())) {
            redirect.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:/usuario/login?error";
        }

        if (twoFAserivice.require2FA(usuario)) {
            session.setAttribute("2faUser", usuario);
            return "redirect:/usuario/2fa";
        }
        authService.signIn(usuario, session);
        return "redirect:/index";
    }
    
    // Login con voz
    @PostMapping("/login/voz")
    public String procesarLoginVoz(@RequestParam String correo,
            @RequestParam String palabraClave,
            HttpSession session,
            RedirectAttributes redirect) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);

        if (usuario == null || !aEncoder.matches(palabraClave, usuario.getPalabraClave())) {
            redirect.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:/usuario/login?error";
        }

     if (twoFAserivice.require2FA(usuario)) {
         session.setAttribute("2faUser", usuario);
            return "redirect:/usuario/2fa/voz";
      }
        authService.signIn(usuario, session);
        return "redirect:/dashboard";
    }
    
// LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.signOut(session);
        return "redirect:/usuario/login?logout";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsI userDetails = (UserDetailsI) auth.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        model.addAttribute("usuario", usuario);
        return "usuario/perfil";
    }
}
