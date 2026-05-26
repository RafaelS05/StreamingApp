package com.pstreaming.controller;

import com.pstreaming.domain.User;
import com.pstreaming.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler{
    
    @Autowired
    private UserService usuarioService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        
        String correo = oAuth2User.getAttribute("email");
        String nombre = oAuth2User.getAttribute("given_name");
        String apellido = oAuth2User.getAttribute("family_name");
        
        User user = usuarioService.getUsuarioByCorreo(correo);
        if (user == null) {
            user = new User();
            user.setEmail(correo);
            user.setName(nombre);
            user.setSurname(apellido);
            //usuarioService.save(rq);
        }
        HttpSession session = request.getSession();
        session.setAttribute("usuarioLogueado", user);
        
        response.sendRedirect("/dashboard");
    }
}
