package com.pstreaming.service;

import com.pstreaming.domain.Rol;
import com.pstreaming.domain.UserDetailsI;
import com.pstreaming.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "MzZiNDcwYzNlYThkNWZjODEyNTZkMzI1MmM3OWYwYTk2Y2U3ZDY1ZWFjZWNmYjllYzg3ZjJiNjc5YjdiNWFkNA==";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretK", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 21600000L);
        ReflectionTestUtils.setField(jwtService, "jwtTempExpiration", 864000L);
    }

    private User usuario(String correo, String rolNombre) {
        User usuario = new User();
        usuario.setEmail(correo);
        usuario.setPassword("hashed");
        Rol rol = new Rol();
        rol.setName(rolNombre);
        usuario.setRol(rol);
        return usuario;
    }

    @Test
    void generateToken_roundTripsUsername() {
        UserDetailsI details = new UserDetailsI(usuario("user@correo.com", "USER"));

        String token = jwtService.generateToken(details);

        assertThat(jwtService.extractUsername(token)).isEqualTo("user@correo.com");
    }

    @Test
    void generateToken_isNotTempToken() {
        UserDetailsI details = new UserDetailsI(usuario("user@correo.com", "USER"));

        String token = jwtService.generateToken(details);

        assertThat(jwtService.isTempToken(token)).isFalse();
    }

    @Test
    void generateTempToken_isMarkedAsTemp() {
        String token = jwtService.generateTempToken(usuario("user@correo.com", "USER"));

        assertThat(jwtService.isTempToken(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("user@correo.com");
    }

    @Test
    void isTokenValid_matchingUser_returnsTrue() {
        UserDetailsI details = new UserDetailsI(usuario("user@correo.com", "USER"));
        String token = jwtService.generateToken(details);

        assertThat(jwtService.isTokenValid(token, details)).isTrue();
    }

    @Test
    void isTokenValid_differentUser_returnsFalse() {
        String token = jwtService.generateToken(new UserDetailsI(usuario("user@correo.com", "USER")));
        UserDetailsI other = new UserDetailsI(usuario("otro@correo.com", "USER"));

        assertThat(jwtService.isTokenValid(token, other)).isFalse();
    }
}
