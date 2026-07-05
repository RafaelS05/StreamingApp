package com.pstreaming;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase separada para el bean de PasswordEncoder.
 * Esto rompe la dependencia circular:
 *   ProjectConfig → JwtAuthenticationFilter → UsuarioService → PasswordEncoder → ProjectConfig
 * Al mover el PasswordEncoder aquí, ProjectConfig ya no necesita ser inicializado
 * antes que JwtAuthenticationFilter.
 */
@Configuration
public class SecurityBeansConfig {

    @Bean
    public PasswordEncoder aEncoder() {
        return new BCryptPasswordEncoder();
    }
}
