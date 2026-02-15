package com.pstreaming.controller;

import com.pstreaming.domain.Usuario;
import com.pstreaming.service.TwoFAService;
import com.pstreaming.service.AuthService;
import com.pstreaming.service.VoiceAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class TwoFactorAuthController {

    @Autowired
    private TwoFAService FAService;
    @Autowired
    private AuthService authService;
    @Autowired
    private VoiceAuthService voiceService;

    @GetMapping("/2fa")
    public String mostrar2FA(HttpSession session, Model model, RedirectAttributes redirect) {
        model.addAttribute("modo", "2fa");

        return "usuario/2fa";
    }

    @PostMapping("/2fa/code")
    public String verificar2FACode(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String codigoIngresado,
            HttpSession session, RedirectAttributes redirect) {

        String codigoEnviado = (String) session.getAttribute("2faCode");
        Usuario usuario = (Usuario) session.getAttribute("2faUser");

        if (codigoIngresado == null || codigoEnviado == null) {
            redirect.addFlashAttribute("error", "Sesión expirada. Por favor, inicie sesión nuevamente.");
            return "redirect:/usuario/login";
        }

        String codigoLimpio = codigoIngresado.trim();
        boolean codigoValido = FAService.verifyCode(usuario.getTelefono(), codigoLimpio, codigoEnviado);

        if (codigoValido) {
            try {
                authService.signIn(usuario, session);
                return "redirect:/index";

            } catch (Exception e) {
                e.printStackTrace();
                redirect.addFlashAttribute("error", "Error interno. Intente nuevamente.");
                return "redirect:/usuario/2fa";
            }
        }

        redirect.addFlashAttribute("error", "Código incorrecto");
        return "redirect:/usuario/2fa";
    }

    //Verify
    @PostMapping("/2fa/voz")
    public String verificarConVoz(@RequestParam("audio") MultipartFile audio,
            HttpServletResponse response,
            HttpSession session, RedirectAttributes redirect) {

        Usuario usuario = (Usuario) session.getAttribute("2faUser");

        if (usuario == null) {
            redirect.addFlashAttribute("error", "Sesión expirada. Por favor, inicie sesión nuevamente.");
            return "redirect:/usuario/login";
        }
        if (audio == null || audio.isEmpty()) {
            redirect.addFlashAttribute("error", "Debe grabar el audio.");
            return "redirect:/usuario/2fa";
        }

        try {
            boolean ok = voiceService.verify(usuario, audio);
            if (!ok) {
                redirect.addFlashAttribute("error", "Voz no coincide.");
                return "redirect:/usuario/2fa";
            }
            authService.signIn(usuario, session);

            session.removeAttribute("2faUser");

            return "redirect:/index";

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error verificando la voz.");
            return "redirect:/usuario/2fa";
        }

    }

    //Apartado de enroll y verify para la voz del usuario
    //Se utilizara el verify como un metodo de verificacion de 2 pasos
    //Enroll
    @GetMapping("/enroll")
    public String mostrarEnroll() {
        return "usuario/vozEnroll";
    }

    @PostMapping("/enroll")
    public String ProcesarEnroll(@RequestParam("audio") MultipartFile audio,
            HttpSession session,
            RedirectAttributes redirect) {

        System.out.println("audio null? " + (audio == null));
        System.out.println("audio empty? " + (audio != null && audio.isEmpty()));
        System.out.println("audio name: " + (audio != null ? audio.getName() : null));
        System.out.println("audio original: " + (audio != null ? audio.getOriginalFilename() : null));
        System.out.println("audio size: " + (audio != null ? audio.getSize() : null));
        System.out.println("audio type: " + (audio != null ? audio.getContentType() : null));
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuario/login";
        }

        try {
            voiceService.enroll(usuario, audio);
            redirect.addFlashAttribute("mensaje", "Voz registrada correctamente.");
            return "redirect:/usuario/perfil";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se pudo registrar la voz.");
            return "redirect:/usuario/voz/enroll";
        }
    }
}
