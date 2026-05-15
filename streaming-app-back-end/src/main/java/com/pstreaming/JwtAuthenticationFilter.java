package com.pstreaming;

import com.pstreaming.domain.*;
import com.pstreaming.service.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Lee el header Authorization de la petición
        String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer " deja pasar la petición
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extrae solo el token quitando el prefijo "Bearer "
            String token = authHeader.substring(7);

            // Extrae el correo del payload del token
            String correo = jwtService.extractUsername(token);

            if (jwtService.isTempToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            // Solo procede si hay correo y todavía no hay autenticación establecida
            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Busca el usuario en la DB con ese correo
                Usuario usuario = usuarioService.getUsuarioByCorreo(correo);
                if (usuario == null) {
                    filterChain.doFilter(request, response);
                    return;
                }
                UserDetailsI userDetails = new UserDetailsI(usuario);

                // Valida que el token sea válido para ese usuario
                if (jwtService.isTokenValid(token, userDetails)) {

                    // Crea el objeto de autenticación con los roles del usuario
                    UsernamePasswordAuthenticationToken authToken
                            = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Adjunta info de la petición actual al contexto de autenticación
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Establece la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
        } catch (Exception e) {
            // Deja continuar y spring devuelve 401
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
