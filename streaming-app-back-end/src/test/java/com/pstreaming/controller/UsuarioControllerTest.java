package com.pstreaming.controller;

import com.pstreaming.domain.MetodoAuth;
import com.pstreaming.domain.Usuario;
import com.pstreaming.dto.UsuarioLoginRequest;
import com.pstreaming.dto.UsuarioLoginResponse;
import com.pstreaming.dto.UsuarioRegistroRequest;
import com.pstreaming.dto.UsuarioResponse;
import com.pstreaming.service.JwtService;
import com.pstreaming.service.TwoFAService;
import com.pstreaming.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;
    @Mock
    private PasswordEncoder aEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private TwoFAService twoFAService;

    @InjectMocks
    private UsuarioController controller;

    private Usuario userWithMethod(String correo, String password, String metodoNombre, Long metodoId) {
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setPassword(password);
        usuario.setNombre("Rafael");
        usuario.setTelefono("+50688887777");
        MetodoAuth metodo = new MetodoAuth();
        metodo.setNombre(metodoNombre);
        metodo.setIdMetodo(metodoId);
        usuario.setMetodoAuth(metodo);
        return usuario;
    }

    // ---------- Registro ----------

    @Test
    void register_returnsCreatedWithBody() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest();
        UsuarioResponse expected = new UsuarioResponse();
        expected.setCorreo("nuevo@correo.com");
        when(usuarioService.save(request)).thenReturn(expected);

        ResponseEntity<UsuarioResponse> response = controller.userRegister(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(expected);
        verify(usuarioService).save(request);
    }

    // ---------- Login con contraseña ----------

    @Test
    void login_userNotFound_returnsUnauthorized() {
        UsuarioLoginRequest request = new UsuarioLoginRequest();
        request.setCorreo("noexiste@correo.com");
        request.setPassword("secret");
        when(usuarioService.getUsuarioByCorreo("noexiste@correo.com")).thenReturn(null);

        ResponseEntity<UsuarioLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).generateToken(any());
        verify(jwtService, never()).generateTempToken(any());
    }

    @Test
    void login_wrongPassword_returnsUnauthorized() {
        UsuarioLoginRequest request = new UsuarioLoginRequest();
        request.setCorreo("user@correo.com");
        request.setPassword("wrong");
        Usuario usuario = userWithMethod("user@correo.com", "hashed", "SMS", 1L);
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(aEncoder.matches("wrong", "hashed")).thenReturn(false);

        ResponseEntity<UsuarioLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_userRoleWithSms_returnsTempTokenAndSendsCode() {
        UsuarioLoginRequest request = new UsuarioLoginRequest();
        request.setCorreo("user@correo.com");
        request.setPassword("secret");
        Usuario usuario = userWithMethod("user@correo.com", "hashed", "SMS", 1L);
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(aEncoder.matches("secret", "hashed")).thenReturn(true);
        when(usuarioService.getRol(usuario)).thenReturn("USER");
        when(jwtService.generateTempToken(usuario)).thenReturn("temp-token");

        ResponseEntity<UsuarioLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UsuarioLoginResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getToken()).isEqualTo("temp-token");
        assertThat(body.getTipo()).isEqualTo("Bearer_TEMP");
        assertThat(body.getMetodoAuth()).isEqualTo(1L);
        verify(twoFAService).sendVerificationCode("user@correo.com", "+50688887777");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_userRoleWithVoice_returnsTempTokenWithoutSms() {
        UsuarioLoginRequest request = new UsuarioLoginRequest();
        request.setCorreo("user@correo.com");
        request.setPassword("secret");
        Usuario usuario = userWithMethod("user@correo.com", "hashed", "VOICE", 2L);
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(aEncoder.matches("secret", "hashed")).thenReturn(true);
        when(usuarioService.getRol(usuario)).thenReturn("USER");
        when(jwtService.generateTempToken(usuario)).thenReturn("temp-token");

        ResponseEntity<UsuarioLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UsuarioLoginResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTipo()).isEqualTo("Bearer_TEMP");
        assertThat(body.getMetodoAuth()).isEqualTo(2L);
        verify(twoFAService, never()).sendVerificationCode(any(), any());
    }

    @Test
    void login_adminRole_returnsFullTokenDirectly() {
        UsuarioLoginRequest request = new UsuarioLoginRequest();
        request.setCorreo("admin@correo.com");
        request.setPassword("secret");
        Usuario usuario = userWithMethod("admin@correo.com", "hashed", "SMS", 1L);
        when(usuarioService.getUsuarioByCorreo("admin@correo.com")).thenReturn(usuario);
        when(aEncoder.matches("secret", "hashed")).thenReturn(true);
        when(usuarioService.getRol(usuario)).thenReturn("ADMIN");
        when(jwtService.generateToken(any())).thenReturn("full-token");

        ResponseEntity<UsuarioLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UsuarioLoginResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getToken()).isEqualTo("full-token");
        assertThat(body.getTipo()).isEqualTo("Bearer");
        assertThat(body.getNombre()).isEqualTo("Rafael");
        assertThat(body.getRol()).isEqualTo("ADMIN");
        verify(jwtService, never()).generateTempToken(any());
        verify(twoFAService, never()).sendVerificationCode(any(), any());
    }
}
