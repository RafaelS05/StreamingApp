package com.pstreaming.controller;

import com.pstreaming.domain.Usuario;
import com.pstreaming.service.TwoFAService;
import com.pstreaming.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder aEncoder;

    @Autowired
    private TwoFAService FAService;

    // Mapeo del Registro
    @GetMapping("/registro")
    public String formRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("isLogin", false);
        model.addAttribute("loginError", false);
        return "usuario/registro";
    }

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

            usuario.setFecha_registro(LocalDateTime.now());

            usuarioService.save(usuario);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado exitosamente");
            return "redirect:/usuario/login";

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "usuario/registro";
        }
    }

    // Mapeo del Login
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("isLogin", true);
            model.addAttribute("loginError", error != null);
        }
        return "usuario/login";
    }

    // PROCESAR LOGIN
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirect) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);

        if (usuario != null && aEncoder.matches(password, usuario.getPassword())) {
            String code = FAService.sendVerificationCode(usuario.getTelefono());

            session.setAttribute("2faCode", code);
            session.setAttribute("2faUser", usuario);

            return "redirect:/usuario/2fa";
        }

        redirect.addAttribute("error", true);
        return "redirect:/usuario/login";
    }

    @GetMapping("/2fa")
    public String mostrar2FA() {
        return "usuario/2fa";

    }

    @PostMapping("/2fa")
    public String verificar2FA(@RequestParam String codigoIngresado,
            HttpSession session, RedirectAttributes redirect) {

        String codigoEnviado = (String) session.getAttribute("2faCode");
        Usuario usuario = (Usuario) session.getAttribute("2faUser");

        if (usuario == null || codigoEnviado == null) {
            redirect.addFlashAttribute("error", "Sesión expirada. Por favor, inicie sesión nuevamente.");
            return "redirect:/usuario/login";
        }

        String codigoLimpio = codigoIngresado.trim();
        boolean codigoValido = FAService.verifyCode(usuario.getTelefono(), codigoLimpio, codigoEnviado);

        if (codigoValido) {
            try {
                usuario.getRoles().size();
                session.setAttribute("usuarioLogueado", usuario);
                session.removeAttribute("2faCode");
                session.removeAttribute("2faUser");

                return "redirect:/dashboard";

            } catch (Exception e) {
                e.printStackTrace();
                redirect.addFlashAttribute("error", "Error interno. Intente nuevamente.");
                return "redirect:/usuario/2fa";
            }
        }

        redirect.addFlashAttribute("error", "Código incorrecto");
        return "redirect:/usuario/2fa";
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login?logout";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "usuario/perfil";
    }

}
