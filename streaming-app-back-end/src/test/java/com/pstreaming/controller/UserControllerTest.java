package com.pstreaming.controller;

import com.pstreaming.domain.AuthMethod;
import com.pstreaming.domain.User;
import com.pstreaming.dto.UserLoginRequest;
import com.pstreaming.dto.UserLoginResponse;
import com.pstreaming.dto.UserRegisterRequest;
import com.pstreaming.dto.UserResponse;
import com.pstreaming.service.JwtService;
import com.pstreaming.service.TwoFAService;
import com.pstreaming.service.UserService;
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
class UserControllerTest {

    @Mock
    private UserService usuarioService;
    @Mock
    private PasswordEncoder aEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private TwoFAService twoFAService;

    @InjectMocks
    private UserController controller;

    private User userWithMethod(String correo, String password, String metodoNombre, Long metodoId) {
        User usuario = new User();
        usuario.setEmail(correo);
        usuario.setPassword(password);
        usuario.setName("Rafael");
        usuario.setPhone("+50688887777");
        AuthMethod metodo = new AuthMethod();
        metodo.setName(metodoNombre);
        metodo.setIdMethod(metodoId);
        usuario.setAuthMethod(metodo);
        return usuario;
    }

    // ---------- Registro ----------

    @Test
    void register_returnsCreatedWithBody() {
        UserRegisterRequest request = new UserRegisterRequest();
        UserResponse expected = new UserResponse();
        expected.setCorreo("nuevo@correo.com");
        when(usuarioService.save(request)).thenReturn(expected);

        ResponseEntity<UserResponse> response = controller.userRegister(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(expected);
        verify(usuarioService).save(request);
    }

    // ---------- Login con contraseña ----------

    @Test
    void login_userNotFound_returnsUnauthorized() {
        UserLoginRequest request = new UserLoginRequest();
        request.setCorreo("noexiste@correo.com");
        request.setPassword("secret");
        when(usuarioService.getUsuarioByCorreo("noexiste@correo.com")).thenReturn(null);

        ResponseEntity<UserLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).generateToken(any());
        verify(jwtService, never()).generateTempToken(any());
    }

    @Test
    void login_wrongPassword_returnsUnauthorized() {
        UserLoginRequest request = new UserLoginRequest();
        request.setCorreo("user@correo.com");
        request.setPassword("wrong");
        User usuario = userWithMethod("user@correo.com", "hashed", "SMS", 1L);
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(aEncoder.matches("wrong", "hashed")).thenReturn(false);

        ResponseEntity<UserLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_userRoleWithSms_returnsTempTokenAndSendsCode() {
        UserLoginRequest request = new UserLoginRequest();
        request.setCorreo("user@correo.com");
        request.setPassword("secret");
        User usuario = userWithMethod("user@correo.com", "hashed", "SMS", 1L);
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(aEncoder.matches("secret", "hashed")).thenReturn(true);
        when(usuarioService.getRol(usuario)).thenReturn("USER");
        when(jwtService.generateTempToken(usuario)).thenReturn("temp-token");

        ResponseEntity<UserLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserLoginResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getToken()).isEqualTo("temp-token");
        assertThat(body.getTipo()).isEqualTo("Bearer_TEMP");
        assertThat(body.getMetodoAuth()).isEqualTo(1L);
        verify(twoFAService).sendVerificationCode("user@correo.com", "+50688887777");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_userRoleWithVoice_returnsTempTokenWithoutSms() {
        UserLoginRequest request = new UserLoginRequest();
        request.setCorreo("user@correo.com");
        request.setPassword("secret");
        User usuario = userWithMethod("user@correo.com", "hashed", "VOICE", 2L);
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(aEncoder.matches("secret", "hashed")).thenReturn(true);
        when(usuarioService.getRol(usuario)).thenReturn("USER");
        when(jwtService.generateTempToken(usuario)).thenReturn("temp-token");

        ResponseEntity<UserLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserLoginResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTipo()).isEqualTo("Bearer_TEMP");
        assertThat(body.getMetodoAuth()).isEqualTo(2L);
        verify(twoFAService, never()).sendVerificationCode(any(), any());
    }

    @Test
    void login_adminRole_returnsFullTokenDirectly() {
        UserLoginRequest request = new UserLoginRequest();
        request.setCorreo("admin@correo.com");
        request.setPassword("secret");
        User usuario = userWithMethod("admin@correo.com", "hashed", "SMS", 1L);
        when(usuarioService.getUsuarioByCorreo("admin@correo.com")).thenReturn(usuario);
        when(aEncoder.matches("secret", "hashed")).thenReturn(true);
        when(usuarioService.getRol(usuario)).thenReturn("ADMIN");
        when(jwtService.generateToken(any())).thenReturn("full-token");

        ResponseEntity<UserLoginResponse> response = controller.userTempLoginPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserLoginResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getToken()).isEqualTo("full-token");
        assertThat(body.getTipo()).isEqualTo("Bearer");
        assertThat(body.getNombre()).isEqualTo("Rafael");
        assertThat(body.getRol()).isEqualTo("ADMIN");
        verify(jwtService, never()).generateTempToken(any());
        verify(twoFAService, never()).sendVerificationCode(any(), any());
    }
}
