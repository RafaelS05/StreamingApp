package com.pstreaming.service;

import com.pstreaming.domain.UserDetailsI;
import com.pstreaming.domain.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public void signIn(Usuario usuario, HttpSession session) {
        UserDetailsI userDetails = new UserDetailsI(usuario);
        UsernamePasswordAuthenticationToken auth
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
        session.setAttribute("usuarioLogueado", usuario);
    }

    public void signOut(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
    }
}
